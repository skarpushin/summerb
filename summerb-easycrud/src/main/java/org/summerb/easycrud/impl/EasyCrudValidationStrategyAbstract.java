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
package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationException;

/**
 * Subclass this class and implement only {@link #doValidateForCreate(Object, ValidationContext)}.
 * It wil be used for both cases Create and update
 *
 * @author sergeyk
 * @param <TRow> row type
 */
public abstract class EasyCrudValidationStrategyAbstract<TRow>
    implements EasyCrudValidationStrategy<TRow> {
  @Override
  public void validateForCreate(TRow dto) throws ValidationException {
    ValidationContext ctx = new ValidationContext();

    doValidateForCreate(dto, ctx);

    if (ctx.isHasErrors()) {
      throw new ValidationException(ctx.getErrors());
    }
  }

  protected abstract void doValidateForCreate(TRow dto, ValidationContext ctx);

  @Override
  public void validateForUpdate(TRow existingVersion, TRow newVersion) throws ValidationException {
    ValidationContext ctx = new ValidationContext();

    doValidateForUpdate(existingVersion, newVersion, ctx);

    if (ctx.isHasErrors()) {
      throw new ValidationException(ctx.getErrors());
    }
  }

  protected void doValidateForUpdate(TRow existingVersion, TRow newVersion, ValidationContext ctx) {
    doValidateForCreate(newVersion, ctx);
  }
  ;
}
