/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.impl;

import java.util.UUID;

import org.summerb.easycrud.api.StringIdGenerator;

/**
 * Simple impl based on {@link UUID} class.
 *
 * @author sergeyk
 */
public class StringIdGeneratorUuidImpl implements StringIdGenerator {
  protected boolean strictUuidMode = false;

  @Override
  public String generateNewId(Object optionalDto) {
    return UUID.randomUUID().toString();
  }

  @Override
  public boolean isValidId(String id) {
    try {
      return id != null && id.length() == 36 && (!strictUuidMode || UUID.fromString(id) != null);
    } catch (IllegalArgumentException exc) {
      return false;
    }
  }

  /**
   * @return If true, will use UUID to verify format. If false will only verify length of the string
   *     which must be 36 char long.
   *     <p>For legacy compatibility is set to false by default.
   */
  public boolean isStrictUuidMode() {
    return strictUuidMode;
  }

  public void setStrictUuidMode(boolean strictUuidMode) {
    this.strictUuidMode = strictUuidMode;
  }
}
