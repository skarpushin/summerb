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
package org.summerb.validation.jakarta;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;

public class JakartaValidationBeanProcessorCachedImpl implements JakartaValidationBeanProcessor {
  protected JakartaValidationBeanProcessor actual;

  protected LoadingCache<Class<?>, List<JakartaValidatorItem>> cache;

  public JakartaValidationBeanProcessorCachedImpl(JakartaValidationBeanProcessor actual) {
    Preconditions.checkArgument(actual != null, "actual required");
    this.actual = actual;

    cache = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Class<?>, List<JakartaValidatorItem>> loader =
      new CacheLoader<>() {
        @Override
        public List<JakartaValidatorItem> load(Class<?> clazz) {
          return actual.getValidationsFor(clazz);
        }
      };

  @Override
  public List<JakartaValidatorItem> getValidationsFor(Class<?> clazz) {
    try {
      return cache.get(clazz);
    } catch (Exception e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      // NOTE: Cannot test this branch -- JakartaValidationBeanProcessor#getValidationsFor is not
      // throwing checked exceptions. But still I want to keep this here just in case.
      throw new RuntimeException("Failed to get validators for " + clazz, e);
    }
  }
}
