package org.summerb.utils.disruptor;

import org.summerb.utils.collection.OneWayList;
import org.summerb.utils.threads.RecurringBackgroundTask;

import com.google.common.base.Preconditions;

/**
 * This class is designed to arrange data processing coming from multiple
 * threads in a way to avoid locking (lock-free) implementation.
 * 
 * Drawback is that a lot of data might be stored in memory waiting while it
 * will be processed and it will result in intensive garbage collection after,
 * but for some cases this is an acceptable behavior
 * 
 * All threads submit data here and this class will arrange data processing in a
 * lock-free way.
 * 
 * @author sergeyk
 *
 */
public class Disruptor<T> {
	private DisruptorProcessor<T> disruptorProcessor;
	private long disruptionInterval;
	private OneWayList<DisruptionQueue<T>> disruptionRing;
	private RecurringBackgroundTask worker;

	public Disruptor(long disruptionInterval, DisruptorProcessor<T> disruptorProcessor) {
		Preconditions.checkArgument(disruptionInterval > 0, "disruptionInterval must be > 0");
		Preconditions.checkArgument(disruptorProcessor != null, "disruptorProcessor must be provided");

		this.disruptionInterval = disruptionInterval;
		this.disruptorProcessor = disruptorProcessor;
		disruptionRing = new OneWayList<DisruptionQueue<T>>();
	}

	private ThreadLocal<DisruptionQueue<T>> disruptionRingThreadLocal = new ThreadLocal<DisruptionQueue<T>>() {
		@Override
		protected DisruptionQueue<T> initialValue() {
			DisruptionQueue<T> newQueue = new DisruptionQueue<T>();
			disruptionRing.appendThreadSafe(newQueue);
			return newQueue;
		};
	};

	public void start() {
		Preconditions.checkState(worker == null, "Worker is already started");
		worker = new RecurringBackgroundTask(workerStep, disruptionInterval);
	}

	public void stop() {
		worker.tearDown(5000);
		worker = null;
	}

	public void submitForProcessing(T data) {
		disruptionRingThreadLocal.get().tasks.append(data);
	}

	private Runnable workerStep = new Runnable() {
		@Override
		public void run() {
			disruptorProcessor.disruptionStepStarted(disruptionInterval);
			for (DisruptionQueue<T> queue : disruptionRing) {
				for (OneWayList<T>.OneWayIterator iter = queue.tasks.iterator(); iter.hasNext();) {
					T data = iter.next();
					if (queue.lastProcessed == data) {
						continue;
					}

					boolean stopProcessingThisThread = false;
					if (disruptorProcessor.isStopProcessingThisThreadData(data)) {
						stopProcessingThisThread = true;
					} else if (!iter.hasNext()) {
						stopProcessingThisThread = true;
					}

					if (stopProcessingThisThread) {
						break;
					} else {
						disruptorProcessor.process(data);
						queue.lastProcessed = data;
						iter.shiftListHead();
					}
				}
			}
			disruptorProcessor.disruptionStepFinished();
		}
	};
}
