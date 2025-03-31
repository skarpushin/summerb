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

import java.util.concurrent.Callable;
import org.summerb.utils.ThrowingRunnable;

public interface ElevationRunner {
  /** Run {@link Runnable} within security context as defined by underlying elevation strategy */
  void run(Runnable runnable);

  /**
   * Run {@link ThrowingRunnable} within security context as defined by underlying elevation
   * strategy
   */
  void runChecked(ThrowingRunnable runnable) throws Exception;

  /**
   * Run {@link Callable} within security context as defined by underlying elevation strategy and
   * return result of call to that Callable
   */
  <T> T call(Callable<T> callable) throws Exception;

  /**
   * Run {@link Callable} within security context as defined by underlying elevation strategy and
   * return result of call to that Callable. In case callable throws checked Exception it will get
   * wrapper into {@link com.google.common.util.concurrent.UncheckedExecutionException}
   */
  <T> T callUnchecked(Callable<T> callable);
}
