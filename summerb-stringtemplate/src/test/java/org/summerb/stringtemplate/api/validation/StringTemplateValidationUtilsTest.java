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
package org.summerb.stringtemplate.api.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.stringtemplate.impl.StringTemplateFactorySpElImpl;
import org.summerb.validation.ValidationContext;

public class StringTemplateValidationUtilsTest {

  @Test
  public void testValidateStringTemplate_blackbox_expectOkForConstant() throws Exception {
    StringTemplateFactory stringTemplateFactory = new StringTemplateFactorySpElImpl();
    String stringTemplate = "Constant text";
    ValidationContext ctx = new ValidationContext();
    String fieldToken = "fieldToken";

    StringTemplateValidationUtils.validateStringTemplate(
        stringTemplateFactory, stringTemplate, ctx, fieldToken);

    assertEquals(false, ctx.isHasErrors());
  }

  @Test
  public void testValidateStringTemplate_blackbox_expectOkForValidExpression() throws Exception {
    StringTemplateFactory stringTemplateFactory = new StringTemplateFactorySpElImpl();
    String stringTemplate = "Constant text plus ${vars['4444']}";
    ValidationContext ctx = new ValidationContext();
    String fieldToken = "fieldToken";

    StringTemplateValidationUtils.validateStringTemplate(
        stringTemplateFactory, stringTemplate, ctx, fieldToken);

    assertEquals(false, ctx.isHasErrors());
  }

  @Test
  public void testValidateStringTemplate_blackbox_expectValidationErrorForWrongExpression()
      throws Exception {
    StringTemplateFactory stringTemplateFactory = new StringTemplateFactorySpElImpl();
    String stringTemplate = "Constant text plus ${vars['4444";
    ValidationContext ctx = new ValidationContext();
    String fieldToken = "fieldToken";

    StringTemplateValidationUtils.validateStringTemplate(
        stringTemplateFactory, stringTemplate, ctx, fieldToken);

    assertEquals(true, ctx.isHasErrors());
  }
}
