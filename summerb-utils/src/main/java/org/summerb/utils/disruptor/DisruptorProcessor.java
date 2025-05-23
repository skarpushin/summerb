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
package org.summerb.utils.disruptor;

public interface DisruptorProcessor<T> {
  void disruptionStepStarted(long disruptionIntervalMs);

  /**
   * Check if processing should continue in this disruption cycle
   *
   * @param data for evaluation purposes
   * @return flow control flag. If false is returned, then that data will not be processed and all
   *     subsequent data in same thread will * not be at this disruption cycle
   */
  boolean isStopProcessingThisThreadData(T data);

  void process(T data);

  void disruptionStepFinished();
}
