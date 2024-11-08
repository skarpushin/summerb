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
package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class LengthMustBeGreaterOrEqual extends ValidationError {
  private static final long serialVersionUID = -1921913462418603691L;

  public static final String MESSAGE_CODE = "validation.length.mustBe.greaterOrEqual";

  /** @deprecated used only for serialization */
  public LengthMustBeGreaterOrEqual() {}

  public LengthMustBeGreaterOrEqual(String propertyName, int border) {
    super(propertyName, MESSAGE_CODE, border);
  }
}
