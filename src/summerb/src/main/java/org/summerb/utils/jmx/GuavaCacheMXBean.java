package org.summerb.utils.jmx;

public interface GuavaCacheMXBean {
	long getRequestCount();

	long getHitCount();

	double getHitRate();

	long getMissCount();

	double getMissRate();

	long getLoadCount();

	long getLoadSuccessCount();

	long getLoadExceptionCount();

	double getLoadExceptionRate();

	long getTotalLoadTime();

	double getAverageLoadPenalty();

	long getEvictionCount();

	long getSize();

	void cleanUp();

	void invalidateAll();

}
