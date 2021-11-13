/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.utils.threads;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.summerb.utils.exceptions.ExceptionUtils;

public class RecurringBackgroundTask implements RecurringBackgroundTaskMXBean {
	private static Logger log = Logger.getLogger(RecurringBackgroundTask.class);

	private Runnable runnable;
	private long delayMs;
	private Thread thread;
	private boolean tearDownRequested;
	private long statsIterations;
	private long statsErrors;
	private long statsInterruptions;
	private String statsLastExceptionMessage;

	public RecurringBackgroundTask(Runnable runnable, long delayMs) {
		this.runnable = runnable;
		this.delayMs = delayMs;

		String threadName = chooseThreadName(runnable);
		thread = new Thread(envelop, threadName);
		thread.setDaemon(true);
		registerMxBean(threadName);
		thread.start();
	}

	private void registerMxBean(String threadName) {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			String name = String.format("%s:type=BackgroundWorker,name=%s", this.getClass().getPackage().getName(),
					threadName);
			ObjectName mxBeanName = new ObjectName(name);
			if (!server.isRegistered(mxBeanName)) {
				server.registerMBean(this, new ObjectName(name));
			}
		} catch (Throwable t) {
			throw new RuntimeException("Failed to register MX bean", t);
		}
	}

	private String chooseThreadName(Runnable runnable) {
		String threadName = runnable.toString();
		threadName = threadName.substring(threadName.lastIndexOf(".") + 1);
		return threadName;
	}

	@SuppressWarnings("deprecation")
	public void tearDown(long joinTimeout) {
		tearDownRequested = true;
		thread.interrupt();
		try {
			thread.join(joinTimeout);
		} catch (InterruptedException e) {
			log.warn("Failed to gracefully tear down thread", e);
			thread.stop();
		}
	}

	private Runnable envelop = new Runnable() {
		@Override
		public void run() {
			try {
				while (!Thread.interrupted() || !tearDownRequested) {
					statsIterations++;
					try {
						// do job itself
						runnable.run();
					} catch (Throwable t) {
						statsErrors++;
						logSafe(t);
					}

					// delay
					try {
						Thread.sleep(delayMs);
					} catch (InterruptedException ie) {
						statsInterruptions++;
						if (tearDownRequested) {
							return;
						} else {
							continue;
						}
					}
				}
			} finally {
				log.info("Envelope finished for " + runnable);
			}
		}

		private void logSafe(Throwable t) {
			try {
				statsLastExceptionMessage = ExceptionUtils.getAllMessagesRaw(t);
				log.error("Iteration failed for " + runnable, t);
			} catch (Throwable exc) {
				// do nothing
			}
		}
	};

	@Override
	public boolean isTearDownRequested() {
		return tearDownRequested;
	}

	@Override
	public long getIterations() {
		return statsIterations;
	}

	@Override
	public long getErrors() {
		return statsErrors;
	}

	@Override
	public long getInterruptions() {
		return statsInterruptions;
	}

	@Override
	public String getLastExceptionMessage() {
		return statsLastExceptionMessage;
	}
}
