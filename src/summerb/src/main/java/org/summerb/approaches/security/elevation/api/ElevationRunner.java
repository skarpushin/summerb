package org.summerb.approaches.security.elevation.api;

import java.util.concurrent.Callable;

public interface ElevationRunner {
	void runElevated(Runnable runnable);

	<T> T callElevated(Callable<T> callable) throws Exception;
}
