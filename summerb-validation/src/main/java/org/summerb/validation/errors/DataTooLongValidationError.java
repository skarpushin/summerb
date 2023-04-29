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

public class DataTooLongValidationError extends ValidationError {
  private static final long serialVersionUID = 7170965971828651975L;

  public static final String VALIDATION_MESSAGE_CODE_TOO_LONG = "validation.data.too.long";
  private int providedSize = -1;
  private int acceptableSize = -1;

  /** @deprecated used only for serialization */
  @Deprecated
  public DataTooLongValidationError() {
    super();
  }

  public DataTooLongValidationError(int dataSize, int acceptableSize, String fieldToken) {
    super(VALIDATION_MESSAGE_CODE_TOO_LONG, fieldToken, dataSize, acceptableSize);

    this.providedSize = dataSize;
    this.acceptableSize = acceptableSize;
  }

  public int getProvidedSize() {
    return providedSize;
  }

  public int getAcceptableSize() {
    return acceptableSize;
  }
}
