/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.concurrent.Callable;
import org.summerb.security.elevation.api.ElevationRunner;
import org.summerb.security.elevation.api.ElevationStrategy;
import org.summerb.utils.ThrowingRunnable;

public class ElevationRunnerImpl implements ElevationRunner {
  protected final ElevationStrategy elevationStrategy;

  public ElevationRunnerImpl(ElevationStrategy elevationStrategy) {
    Preconditions.checkArgument(elevationStrategy != null);
    this.elevationStrategy = elevationStrategy;
  }

  @Override
  public void run(Runnable runnable) {
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
  public void runChecked(ThrowingRunnable runnable) throws Exception {
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
  public <T> T call(Callable<T> callable) throws Exception {
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

  @Override
  public <T> T callUnchecked(Callable<T> callable) {
    boolean elevationRequired = elevationStrategy.isElevationRequired();
    Object cookie = null;
    if (elevationRequired) {
      cookie = elevationStrategy.elevate();
    }

    try {
      return callable.call();
    } catch (Exception e) {
      throw new UncheckedExecutionException("Underlying call threw an exception", e);
    } finally {
      if (elevationRequired) {
        elevationStrategy.deElevate(cookie);
      }
    }
  }
}
