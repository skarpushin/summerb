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
package org.summerb.security.elevation.api;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.concurrent.Callable;

public interface ElevationRunner {
  void runElevated(Runnable runnable);

  <T> T callElevated(Callable<T> callable) throws Exception;

  default <T> T callElevatedUnchecked(Callable<T> callable) {
    try {
      return callElevated(callable);
    } catch (Exception e) {
      throw new UncheckedExecutionException(
          "Underlying call to callElevated() threw an exception", e);
    }
  }
}
