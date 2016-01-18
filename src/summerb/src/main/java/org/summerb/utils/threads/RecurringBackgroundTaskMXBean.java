package org.summerb.utils.threads;

public interface RecurringBackgroundTaskMXBean {
	boolean isTearDownRequested();

	long getIterations();

	long getErrors();

	long getInterruptions();

	String getLastExceptionMessage();
}
