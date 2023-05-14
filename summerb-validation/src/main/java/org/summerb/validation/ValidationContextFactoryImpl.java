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
package org.summerb.validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.methodCapturers.PropertyNameObtainer;
import org.summerb.methodCapturers.PropertyNameObtainerFactory;
import org.summerb.validation.jakarta.JakartaValidator;

import com.google.common.base.Preconditions;

public class ValidationContextFactoryImpl implements ValidationContextFactory {

  protected final JakartaValidator jakartaValidator;
  protected final PropertyNameObtainerFactory propertyNameObtainerFactory;

  public ValidationContextFactoryImpl(
      @Nonnull PropertyNameObtainerFactory propertyNameObtainerFactory,
      @Nullable JakartaValidator jakartaValidator) {
    Preconditions.checkArgument(propertyNameObtainerFactory != null);
    this.propertyNameObtainerFactory = propertyNameObtainerFactory;
    this.jakartaValidator = jakartaValidator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull <T, F extends ValidationContext<T>> F buildFor(@Nonnull T bean) {
    Preconditions.checkArgument(bean != null);
    try {
      PropertyNameObtainer<T> obtainer =
          propertyNameObtainerFactory.getObtainer((Class<T>) bean.getClass());
      return (F) new ValidationContext<T>(bean, obtainer, jakartaValidator, this);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build ValidationContext for " + bean, e);
    }
  }

  @Override
  public @Nonnull ValidationContext<?> build() {
    return new ValidationContext<>();
  }
}
