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
package org.summerb.users.api.validation;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class DuplicateUserValidationError extends ValidationError {
  @Serial private static final long serialVersionUID = -2231143102381068894L;

  public static final String VALIDATION_MESSAGE_CODE_DUPLICATE_USER = "duplicate.user";

  /**
   * @deprecated used only for serialization
   */
  @Deprecated
  public DuplicateUserValidationError() {
    super();
  }

  public DuplicateUserValidationError(String fieldToken) {
    super(fieldToken, VALIDATION_MESSAGE_CODE_DUPLICATE_USER);
  }
}
