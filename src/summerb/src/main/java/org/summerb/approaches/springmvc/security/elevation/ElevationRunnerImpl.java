package org.summerb.approaches.springmvc.security.elevation;

import java.util.concurrent.Callable;

import org.summerb.approaches.springmvc.security.apis.ElevationRunner;
import org.summerb.approaches.springmvc.security.apis.ElevationStrategy;

import com.google.common.base.Preconditions;

public class ElevationRunnerImpl implements ElevationRunner {
	private ElevationStrategy elevationStrategy;

	public ElevationRunnerImpl(ElevationStrategy elevationStrategy) {
		Preconditions.checkArgument(elevationStrategy != null);
		this.elevationStrategy = elevationStrategy;
	}

	@Override
	public void runElevated(Runnable runnable) {
		boolean elevationRequired = elevationStrategy.isElevationRequired();
		Object cookie = null;
		if (elevationRequired) {
			cookie = elevationStrategy.elevate();
		}

		try {
			runnable.run();
		} finally {
			if (elevationRequired) {
				elevationStrategy.deElevate(cookie);
			}
		}
	}

	@Override
	public <T> T callElevated(Callable<T> callable) throws Exception {
		boolean elevationRequired = elevationStrategy.isElevationRequired();
		Object cookie = null;
		if (elevationRequired) {
			cookie = elevationStrategy.elevate();
		}

		try {
			return callable.call();
		} finally {
			if (elevationRequired) {
				elevationStrategy.deElevate(cookie);
			}
		}
	}

}
