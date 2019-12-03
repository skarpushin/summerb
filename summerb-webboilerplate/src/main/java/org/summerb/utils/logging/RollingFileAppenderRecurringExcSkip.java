/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.utils.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.log4j.MDC;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.web.context.WebApplicationContext;
import org.summerb.security.api.CurrentUserResolver;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.users.api.dto.User;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

/**
 * This appender is smart enough to avoid duplicate logging of same exceptions.
 * It tracks seen exceptions and just count number of recurring exceptions.
 * 
 * Also there is a method {@link #getStatistics()} which allows to get current
 * exc statistics.. It's a thread-safe thing.
 * 
 * Internally logger ensure to be non-blocking for write operations - all
 * analysis is done in separate thread so all log write are lock-free
 * operations.
 * 
 * IMPORTANT: {@link #close()} method is never called by container, that's why I
 * had to create {@link RollingFileAppenderRecurringExcSkipCloser} that will
 * close this logger
 * 
 * @author sergeyk
 *
 */
public final class RollingFileAppenderRecurringExcSkip extends RollingFileAppender {
	private static final String MDC_EXC_CODE = "excCode";

	private static RollingFileAppenderRecurringExcSkip INSTANCE;

	private ConcurrentLinkedDeque<RecordEventParameter> records = new ConcurrentLinkedDeque<RecordEventParameter>();
	private volatile Thread statisticsProcessingThread;
	private volatile boolean interruptThread;
	private Map<String, FeedbackExceptionInfo> excMap = new HashMap<>();
	private volatile Set<String> knownExcCodes = new HashSet<String>();
	private Object excMapSyncRoot = new Object();
	private CurrentUserResolver<User> currentUserResolver;

	public RollingFileAppenderRecurringExcSkip() {
		statisticsProcessingThread = new Thread(statisticsProcessingWorker, "Logger Statistics Processing");
		statisticsProcessingThread.setDaemon(true);
		statisticsProcessingThread.start();
		INSTANCE = this;
	}

	@Override
	public synchronized void close() {
		ensureThreadStopped();
		super.close();
	}

	private void ensureThreadStopped() {
		interruptThread = true;
		Thread threadToStop = statisticsProcessingThread;
		statisticsProcessingThread = null;
		if (threadToStop != null) {
			try {
				threadToStop.join(5000);
			} catch (InterruptedException e) {
				// that's ok
			}
		}
	}

	public static RollingFileAppenderRecurringExcSkip getInstance() {
		return INSTANCE;
	}

	private Runnable statisticsProcessingWorker = new Runnable() {
		@Override
		public void run() {
			try {
				while (!interruptThread) {
					try {
						long recordsProcessed = analyzeRecordsIfAny();
						long timeToSleep = decideTimeToSleep(recordsProcessed);
						Thread.sleep(timeToSleep);
					} catch (InterruptedException ie) {
						break;
					} catch (Throwable t) {
						// not sure what to do... we're in logger. if issue
						// relates to logging and we'll try to log it again -
						// stack overflow is possible. SO we'll just break and
						// stop analyzing logs
						break;
					}
				}
			} finally {
				statisticsProcessingThread = null;
			}
		}
	};

	/**
	 * @return true if this exception first seen
	 */
	private boolean noticeRecord(RecordEventParameter params) {
		FeedbackExceptionInfo exInfo = excMap.get(params.exLocationCode);
		boolean added = false;
		if (exInfo == null) {
			exInfo = new FeedbackExceptionInfo();
			exInfo.setId(params.exLocationCode);
			exInfo.setMsgs(ExceptionUtils.getThrowableStackTraceAsString(params.throwable));
			synchronized (excMapSyncRoot) {
				excMap.put(params.exLocationCode, exInfo);
			}
			added = true;
		}
		exInfo.setCount(exInfo.getCount() + 1);
		noticeCurrentUserNameIfAny(params, exInfo);
		return added;
	}

	private void noticeCurrentUserNameIfAny(RecordEventParameter params, FeedbackExceptionInfo exInfo) {
		if (params.user == null) {
			return;
		}

		if (exInfo.getAffectedUsers() == null) {
			exInfo.setAffectedUsers(new HashSet<String>());
		}
		exInfo.getAffectedUsers().add(params.user);
	}

	private long analyzeRecordsIfAny() {
		long processed = 0;
		RecordEventParameter next = null;
		LinkedList<String> newExcCodes = null;
		while ((next = records.poll()) != null) {
			processed++;
			if (noticeRecord(next)) {
				if (newExcCodes == null) {
					newExcCodes = new LinkedList<String>();
				}
				newExcCodes.add(next.exLocationCode);
			}
		}
		if (newExcCodes != null) {
			HashSet<String> newSet = new HashSet<String>(knownExcCodes);
			newSet.addAll(newExcCodes);
			knownExcCodes = newSet;
		}
		return processed;
	}

	private long decideTimeToSleep(long recordsProcessed) {
		if (recordsProcessed > 5) {
			return 50;
		}
		return 500;
	}

	public List<FeedbackExceptionInfo> getStatistics() {
		synchronized (excMapSyncRoot) {
			List<FeedbackExceptionInfo> ret = new ArrayList<FeedbackExceptionInfo>(excMap.values().size());
			for (FeedbackExceptionInfo ei : excMap.values()) {
				FeedbackExceptionInfo n = new FeedbackExceptionInfo();
				n.setId(ei.getId());
				n.setCount(ei.getCount());
				n.setMsgs(ei.getMsgs());
				if (ei.getAffectedUsers() != null) {
					n.setAffectedUsers(new HashSet<>(ei.getAffectedUsers()));
				}
				ret.add(n);
			}
			return ret;
		}
	}

	@Override
	public void append(LoggingEvent le) {
		if (interruptThread) {
			super.append(le);
			return;
		}

		String exLocationCode = enqueueLogginEventForAnalysis(le);
		if (knownExcCodes.contains(exLocationCode)) {
			return;
		}
		if (exLocationCode != null) {
			MDC.put(MDC_EXC_CODE, "[excCode=" + exLocationCode + "]: ");
		} else {
			// it might be there after previous append, ensure to clean up
			MDC.remove(MDC_EXC_CODE);
		}
		super.append(le);
		MDC.remove(MDC_EXC_CODE);
	}

	private String enqueueLogginEventForAnalysis(LoggingEvent le) {
		if (statisticsProcessingThread == null) {
			return null;
		}

		Throwable throwable = le.getThrowableInformation() == null ? null : le.getThrowableInformation().getThrowable();
		if (throwable == null) {
			// not interested to analyze throwable-free events
			return null;
		}

		String exLocationCode = null;
		try {
			exLocationCode = ExceptionUtils.calculateExceptionCode(throwable);
		} catch (Throwable t) {
			System.out.println("Logger failed");
			t.printStackTrace();
		}
		if (exLocationCode != null) {
			// TBD: Also record query string if possible (if it's a request
			// processing stage and not some kind of background process). If
			// query string is not available then use thread name and
			// class::method where exception originated from
			records.offer(new RecordEventParameter(throwable, exLocationCode, tryIdentifyUser()));
		}
		return exLocationCode;
	}

	private String tryIdentifyUser() {
		String userName = tryGetUserName();
		if (userName != null) {
			return userName;
		}

		// NOTE: That is actually very questionable part. We're assuming ip might be in
		// MDC under some hardcoded key...
		Object ip = MDC.get("x_ip");
		if (ip != null) {
			return String.valueOf(ip);
		}

		return null;
	}

	private String tryGetUserName() {
		CurrentUserResolver<User> userResolver = getCurrentUserResolver();
		if (userResolver == null) {
			return null;
		}

		try {
			User user = userResolver.getUser();
			if (user == null || user.getEmail() == null) {
				return null;
			}

			return user.getEmail();
		} catch (Throwable t) {
			// intentionally ignore
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public CurrentUserResolver<User> getCurrentUserResolver() {
		if (currentUserResolver == null) {
			try {
				WebApplicationContext wac = CurrentRequestUtils.getWac();
				if (wac == null) {
					return null;
				}
				currentUserResolver = wac.getBean(SecurityContextResolver.class);
			} catch (Throwable t) {
				// intentionally ignore it
			}
		}
		return currentUserResolver;
	}

	public void setCurrentUserResolver(CurrentUserResolver<User> currentUserResolver) {
		this.currentUserResolver = currentUserResolver;
	}

	private static class RecordEventParameter {
		public Throwable throwable;
		public String exLocationCode;
		public String user;

		public RecordEventParameter(Throwable throwable, String exLocationCode, String user) {
			this.throwable = throwable;
			this.exLocationCode = exLocationCode;
			this.user = user;
		}
	}

}
