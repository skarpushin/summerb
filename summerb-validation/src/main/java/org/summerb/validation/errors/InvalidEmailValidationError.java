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

public class InvalidEmailValidationError extends ValidationError {
  private static final long serialVersionUID = -8119415446133607944L;

  public static final String VALIDATION_MESSAGE_CODE_INVALID_FORMAT_EMAIL =
      "validation.invalid.email.format";

  /** @deprecated used only for serialization */
  @Deprecated
  public InvalidEmailValidationError() {
    super();
  }

  public InvalidEmailValidationError(String fieldToken) {
    super(VALIDATION_MESSAGE_CODE_INVALID_FORMAT_EMAIL, fieldToken);
  }
}
