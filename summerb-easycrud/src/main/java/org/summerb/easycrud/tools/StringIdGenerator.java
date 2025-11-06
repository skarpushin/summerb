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
package org.summerb.easycrud.tools;

public interface StringIdGenerator {

  /**
   * Generate new ID for given DTO. ID doesn't have to depend on DTO, it's purely Voluntarily.
   *
   * @param optionalDto dto to create ID for, could be null
   * @return Some unique string that represents ID
   */
  String generateNewId(Object optionalDto);

  /**
   * Check if specific DTO confirms to the format used by this specific implementation
   *
   * @param id not null/not empty id
   * @return true if valid
   */
  boolean isValidId(String id);
}
