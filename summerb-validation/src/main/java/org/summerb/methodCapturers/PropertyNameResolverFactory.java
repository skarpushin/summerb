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
package org.summerb.methodCapturers;

/**
 * This factory is responsible to return PropertyName obtainers.
 *
 * <p>Impl is supposed to be cached to overcome reflection cost
 *
 * @author Sergey Karpushin
 */
public interface PropertyNameResolverFactory {

  <T> PropertyNameResolver<T> getResolver(Class<T> beanClass);
}
