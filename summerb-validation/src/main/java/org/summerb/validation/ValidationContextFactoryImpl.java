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
package org.summerb.validation;

import com.google.common.base.Preconditions;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.validation.jakarta.JakartaValidator;

public class ValidationContextFactoryImpl implements ValidationContextFactory {

  protected final JakartaValidator jakartaValidator;
  protected final PropertyNameResolverFactory propertyNameResolverFactory;

  public ValidationContextFactoryImpl(
      PropertyNameResolverFactory propertyNameResolverFactory, JakartaValidator jakartaValidator) {
    Preconditions.checkArgument(propertyNameResolverFactory != null);
    this.propertyNameResolverFactory = propertyNameResolverFactory;
    this.jakartaValidator = jakartaValidator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T, F extends ValidationContext<T>> F buildFor(T bean) {
    Preconditions.checkArgument(bean != null);
    try {
      PropertyNameResolver<T> obtainer =
          propertyNameResolverFactory.getResolver((Class<T>) bean.getClass());
      return (F) new ValidationContext<>(bean, obtainer, jakartaValidator, this);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build ValidationContext for " + bean, e);
    }
  }

  @Override
  public ValidationContext<?> build() {
    return new ValidationContext<>(jakartaValidator, this);
  }
}
