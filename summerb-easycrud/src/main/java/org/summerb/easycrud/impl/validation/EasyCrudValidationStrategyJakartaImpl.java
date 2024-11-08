/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.impl.validation;

import javax.validation.constraints.AssertTrue;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationContextConfig;
import org.summerb.validation.jakarta.processors.AssertTrueProcessor;

/**
 * This impl will just delegate validation to Jakarta Annotations (i.e. {@link AssertTrue}). But (!)
 * these annotations will be processed by SummerB Validation processors (i.e.{@link
 * AssertTrueProcessor}) and result in invocation of relevant methods of {@link ValidationContext},
 * so semantics on {@link ValidationContext} usage will be retained.
 *
 * <p>IMPORTANT: Make sure your class is created as a Spring Bean so that {@link
 * #validationContextFactory} will be autowired (in order for autowiring to work, make sure you
 * import {@link ValidationContextConfig}).
 *
 * @author sergeyk
 * @param <TRow> row type
 */
public class EasyCrudValidationStrategyJakartaImpl<TRow>
    extends EasyCrudValidationStrategyAbstract<TRow> {

  @Override
  protected void validate(TRow row, ValidationContext<TRow> ctx) {
    ctx.processJakartaValidations();
  }
}
