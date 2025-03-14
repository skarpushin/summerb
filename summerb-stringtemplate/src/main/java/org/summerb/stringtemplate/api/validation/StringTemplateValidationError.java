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
package org.summerb.stringtemplate.api.validation;

import org.summerb.validation.ValidationError;

public class StringTemplateValidationError extends ValidationError {
  private static final long serialVersionUID = -7110646530687463433L;

  public static final String VALIDATION_MESSAGE_CODE_COMPILATION_ERROR =
      "validation.stringtemplate.compilationError";

  private Throwable cause;

  /**
   * @deprecated only for io
   */
  @Deprecated
  public StringTemplateValidationError() {}

  public StringTemplateValidationError(String fieldToken, Throwable cause) {
    super(fieldToken, VALIDATION_MESSAGE_CODE_COMPILATION_ERROR);
    this.cause = cause;

    // TBD: Probably add more sophisticated analysis of error, to be able
    // to provide user with more details
  }

  public Throwable getCause() {
    return cause;
  }
}
