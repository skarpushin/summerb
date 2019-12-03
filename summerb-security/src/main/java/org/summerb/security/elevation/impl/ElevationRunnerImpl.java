/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.security.elevation.impl;

import java.util.concurrent.Callable;

import org.summerb.security.elevation.api.ElevationRunner;
import org.summerb.security.elevation.api.ElevationStrategy;

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
