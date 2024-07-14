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
package org.summerb.easycrud.impl.validation;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationContextConfig;
import org.summerb.validation.ValidationContextFactory;

/**
 * Use this abstract class to implement {@link EasyCrudValidationStrategy} and avoid majority of
 * boiler plate code.
 *
 * <p>if you implement only {@link #validate(Object, ValidationContext)}, then it will be used for
 * both - create and update. If you have separate logic for update, then please:
 *
 * <ul>
 *   <li>also override {@link #validateForUpdate(Object, Object, ValidationContext)}
 *   <li>and override {@link #isCurrentlyPersistedRowNeededForUpdateValidation()} to tell EasyCrud
 *       that currently persisted version of the row needed to be retrieved from DB and passed to
 *       the first argument of {@link #validateForUpdate(Object, Object)}
 * </ul>
 *
 * <p>IMPORTANT: Make sure your class is created as a Spring Bean so that {@link
 * #validationContextFactory} will be autowired (in order for autowiring to work, make sure you
 * import {@link ValidationContextConfig}) or construct neccessary beans yourself.
 *
 * @author sergeyk
 * @param <TRow> row type
 */
public abstract class EasyCrudValidationStrategyAbstract<TRow>
    implements EasyCrudValidationStrategy<TRow>, InitializingBean {

  @Autowired protected ValidationContextFactory validationContextFactory;

  @Override
  public void afterPropertiesSet() {
    Preconditions.checkArgument(
        validationContextFactory != null, "validationContextFactory required");
  }

  @Override
  public void validateForCreate(TRow row) {
    Preconditions.checkState(
        validationContextFactory != null,
        "validationContextFactory must be set before using this method");
    var ctx = validationContextFactory.buildFor(row);
    validate(row, ctx);
    ctx.throwIfHasErrors();
  }

  protected abstract void validate(TRow row, ValidationContext<TRow> ctx);

  @Override
  public void validateForUpdate(TRow existingVersion, TRow newVersion) {
    Preconditions.checkState(
        validationContextFactory != null,
        "validationContextFactory must be set before using this method");

    var ctx = validationContextFactory.buildFor(newVersion);
    validateForUpdate(existingVersion, newVersion, ctx);
    ctx.throwIfHasErrors();
  }

  @Override
  public boolean isCurrentlyPersistedRowNeededForUpdateValidation() {
    return false;
  }

  /**
   * Validate update operation. By default {@link #validate(Object, ValidationContext)} will be used
   * (same one as for create operation) But if you need to validate update logic, pelase override
   * this method and provide impl
   *
   * @param existingVersion currently persisted version in DB
   * @param row new version that is about to be persisted to DB
   * @param ctx {@link ValidationContext}
   */
  protected void validateForUpdate(TRow existingVersion, TRow row, ValidationContext<TRow> ctx) {
    validate(row, ctx);
  }
}
