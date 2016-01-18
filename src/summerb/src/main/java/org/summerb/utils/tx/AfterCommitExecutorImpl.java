package org.summerb.utils.tx;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class AfterCommitExecutorImpl extends TransactionSynchronizationAdapter implements Executor {
	private static Logger log = Logger.getLogger(AfterCommitExecutorImpl.class);

	private ExecutorService executorService;

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

		// NOTE: We do it in separate thread to ensure they will be execute
		// outside of current transaction.Guaranteed
		executorService.submit(new RunRunnables(threadRunnables));
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

	public ExecutorService getExecutorService() {
		return executorService;
	}

	@Required
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
