package org.summerb.approaches.springmvc.security.apis;

import java.util.concurrent.Callable;

public interface ElevationRunner {
	void runElevated(Runnable runnable);

	<T> T callElevated(Callable<T> callable) throws Exception;
}
