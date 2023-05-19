/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.validation;

public interface ValidationContextFactory {

  /**
   * @param <T> type of Bean
   * @param <F> typed ValidationContext
   * @param bean Bean which getters will be used to extract field names and values
   * @return instance that can be used for both - referring to fields using method references as
   *     well as string literals
   */
  <T, F extends ValidationContext<T>> F buildFor(T bean);

  /**
   * @return instance that can be used only to refer to fields using string literals. Not
   *     recommended as in such case you'll use string literals and loose all power of IDE and
   *     Compiler static code analysis
   */
  ValidationContext<?> build();
}
