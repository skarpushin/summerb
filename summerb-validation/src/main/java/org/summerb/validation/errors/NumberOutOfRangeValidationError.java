/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

public class NumberOutOfRangeValidationError extends ValidationError {
  private static final long serialVersionUID = 1159149569870010322L;

  /** @deprecated used only for serialization */
  @Deprecated
  public NumberOutOfRangeValidationError() {
    super();
  }

  public NumberOutOfRangeValidationError(
      long subject, long lowerBorder, long upperBorder, String fieldToken) {
    super("validation.numberOutOfRange", fieldToken, subject, lowerBorder, upperBorder);
  }
}
