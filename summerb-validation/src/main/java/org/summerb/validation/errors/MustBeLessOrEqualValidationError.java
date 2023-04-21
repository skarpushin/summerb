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

public class MustBeLessOrEqualValidationError extends ValidationError {
  private static final long serialVersionUID = -1520235304162692765L;

  private Number subject;
  private Number border;

  /** @deprecated used only for serialization */
  @Deprecated
  public MustBeLessOrEqualValidationError() {
    super();
  }

  public MustBeLessOrEqualValidationError(Number subject, Number border, String fieldToken) {
    super("validation.mustBeLessOrEq", fieldToken, subject, border);

    this.subject = subject;
    this.border = border;
  }

  public Number getSubject() {
    return subject;
  }

  public Number getBorder() {
    return border;
  }
}
