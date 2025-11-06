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
package org.summerb.easycrud.scaffold;

import java.lang.reflect.Method;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.dao.EasyCrudDaoInjections;
import org.summerb.easycrud.row.HasId;

/**
 * Impl of this interface will be responsible for creating impls for methods of sub-interfaces of
 * {@link EasyCrudService} marked with {@link Query} annotation and instantiated using {@link
 * EasyCrudScaffold#fromService(Class, String, String, Object...)}
 *
 * @author sergeyk
 */
public interface ScaffoldedMethodFactory {

  CallableMethod create(
      EasyCrudService<?, HasId<?>> service, EasyCrudDaoInjections<?, HasId<?>> dao, Method method);
}
