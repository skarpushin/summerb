package org.summerb.utils.disruptor;

import org.summerb.utils.collection.OneWayList;

public class DisruptionQueue<T> {
	protected OneWayList<T> tasks = new OneWayList<T>();
	protected T lastProcessed;
}
