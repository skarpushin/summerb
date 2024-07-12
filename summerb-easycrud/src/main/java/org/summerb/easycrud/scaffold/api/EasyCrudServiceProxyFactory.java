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
package org.summerb.easycrud.scaffold.api;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoInjections;

/**
 * Factory for Proxies of custom sub-interfaces of {@link EasyCrudService}
 *
 * @author sergeyk
 */
public interface EasyCrudServiceProxyFactory {

  /**
   * @param optionalDao needs to be provided only if {@link ScaffoldedQuery} are used
   */
  <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> TService createProxy(
      Class<TService> serviceInterface,
      EasyCrudService<TId, TDto> service,
      EasyCrudDaoInjections<TId, TDto> optionalDao);
}
