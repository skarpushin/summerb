package org.summerb.utils.disruptor;

public interface DisruptorProcessor<T> {
	void disruptionStepStarted(long disruptionIntervalMs);

	/**
	 * if false returned that data will not be processed and all subsequebt data
	 * in same thread will not be at this disruption cycle
	 * 
	 * @param data
	 * @return
	 */
	boolean isStopProcessingThisThreadData(T data);

	void process(T data);

	void disruptionStepFinished();
}
