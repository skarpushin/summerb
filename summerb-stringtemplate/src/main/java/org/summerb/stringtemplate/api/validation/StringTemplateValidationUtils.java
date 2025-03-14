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

import com.google.common.base.Preconditions;
import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.validation.ValidationContext;

/**
 * Simple utility class which provides validation function(s) for string templates
 *
 * @author skarpushin
 */
public class StringTemplateValidationUtils {

  /**
   * Validate string template basically using compiler. If compile successful, that template
   * validates fine
   *
   * @param stringTemplateFactory compiler to validate against
   * @param stringTemplate template to validate
   * @param ctx validation context
   * @param fieldToken field token
   */
  public static void validateStringTemplate(
      StringTemplateFactory stringTemplateFactory,
      String stringTemplate,
      ValidationContext<?> ctx,
      String fieldToken) {

    Preconditions.checkArgument(stringTemplateFactory != null);
    Preconditions.checkArgument(ctx != null);

    try {
      stringTemplateFactory.build(stringTemplate);
    } catch (Throwable t) {
      ctx.getErrors().add(new StringTemplateValidationError(fieldToken, t));
    }
  }
}
