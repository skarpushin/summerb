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
package org.summerb.webappboilerplate.security.ve;

import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.validation.ValidationError;
import org.summerb.webappboilerplate.security.dto.PasswordReset;

public class PasswordsDontMatchValidationError extends ValidationError {
  private static final long serialVersionUID = 6392441057143489663L;

  public PasswordsDontMatchValidationError() {
    super(
        SecurityMessageCodes.VALIDATION_PASSWORDS_DO_NOT_MATCH,
        PasswordReset.FN_NEW_PASSWORD_AGAIN);
  }
}
