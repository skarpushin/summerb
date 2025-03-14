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
package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationError;

/**
 * There is no method in {@link ValidationContext} that checks for this error. You can add this
 * manually based on your business logic.
 *
 * @author Sergey Karpushin
 */
public class MustNotHaveDuplicateName extends ValidationError {
  @Serial private static final long serialVersionUID = -537217996301287218L;

  public static final String MESSAGE_CODE = "validation.mustNot.haveDuplicateName";

  /**
   * @deprecated used only for serialization
   */
  public MustNotHaveDuplicateName() {}

  public MustNotHaveDuplicateName(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
