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
package org.summerb.utils.tx;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This is the preferred impl of {@link Executor} - it uses thread local to
 * store all runnables which supposed to be executed only when transaction is
 * committed
 * 
 * @author sergeyk
 *
 */
public class AfterCommitExecutorThreadLocalImpl extends TransactionSynchronizationAdapter implements Executor {
	private static Logger log = LogManager.getLogger(AfterCommitExecutorThreadLocalImpl.class);

	private static final ThreadLocal<Queue<Runnable>> RUNNABLES = new ThreadLocal<Queue<Runnable>>();

	@Override
	public void execute(Runnable runnable) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			if (log.isDebugEnabled()) {
				log.debug("Transaction synchronization is NOT ACTIVE. Executing runnable right now " + runnable);
			}
			safeRun(runnable);
			return;
		}
		Queue<Runnable> threadRunnables = RUNNABLES.get();
		if (threadRunnables == null) {
			threadRunnables = new LinkedList<Runnable>();
			RUNNABLES.set(threadRunnables);
			TransactionSynchronizationManager.registerSynchronization(this);
		}
		if (log.isDebugEnabled()) {
			log.debug("Submitting new runnable to run after commit: " + runnable);
		}
		threadRunnables.offer(runnable);
	}

	@Override
	public void afterCommit() {
		Queue<Runnable> threadRunnables = RUNNABLES.get();
		if (threadRunnables.size() == 0) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Transaction successfully committed, executing runnables #" + threadRunnables.size());
		}

		// TBD: Explain why we should do it async as it was impl before. WHy we can't
		// just run it now
		new RunRunnables(threadRunnables).run();

		// NOTE: We do it in separate thread to ensure they will be execute
		// outside of current transaction.Guaranteed
		// executorService.submit(new RunRunnables(threadRunnables));
		// No need to wait for it actually since handlers are normally outside
		// of this process boundary
		// Future<?> future = executorService.submit(new
		// RunRunnables(threadRunnables));
		// try {
		// future.get();
		// } catch (Throwable t) {
		// log.warn("Failed to execute deferred runnables after transsaction
		// commit", t);
		// }
	}

	private static class RunRunnables implements Runnable {
		private Queue<Runnable> threadRunnables;

		public RunRunnables(Queue<Runnable> threadRunnables) {
			this.threadRunnables = threadRunnables;
		}

		@Override
		public void run() {
			Runnable runnable = null;
			while ((runnable = threadRunnables.poll()) != null) {
				if (log.isDebugEnabled()) {
					log.debug("Executing runnable after TX commit" + runnable);
				}
				safeRun(runnable);
			}
		}
	}

	private static void safeRun(Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable e) {
			log.error("Failed to execute runnable on transaction commit " + runnable, e);
		}
	}

	@Override
	public void afterCompletion(int status) {
		RUNNABLES.remove();
		if (log.isDebugEnabled()) {
			String result = status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK";
			log.debug("Transaction completed with status " + result);
		}
	}
}
