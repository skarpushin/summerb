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
package org.summerb.easycrud.rest.permissions;

import java.util.HashMap;
import java.util.Map;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

import com.google.common.base.Preconditions;

public class PermissionsResolverStrategyRowAuthImpl<TId, TDto extends HasId<TId>>
    implements PermissionsResolverStrategy<TId, TDto> {

  private PermissionsResolverPerRow<TId, TDto> authStrategy;

  public PermissionsResolverStrategyRowAuthImpl(PermissionsResolverPerRow<TId, TDto> authStrategy) {
    Preconditions.checkArgument(authStrategy != null, "authStrategy must not be null");
    this.authStrategy = authStrategy;
  }

  @Override
  public void resolvePermissions(
      MultipleItemsResult<TId, TDto> ret, PathVariablesMap contextVariables) {
    ret.setRowPermissions(new HashMap<>());
    for (TDto row : ret.getRows()) {
      Map<String, Boolean> rowPerms = new HashMap<>();
      ret.getRowPermissions().put(row.getId(), rowPerms);
      resolve(row, contextVariables, rowPerms);
    }

    ret.setTablePermissions(new HashMap<>());
    resolve(null, contextVariables, ret.getTablePermissions());
  }

  @Override
  public void resolvePermissions(SingleItemResult<TId, TDto> ret) {
    ret.setPermissions(new HashMap<>());
    resolve(ret.getRow(), null, ret.getPermissions());
  }

  protected void resolve(TDto dto, PathVariablesMap contextVariables, Map<String, Boolean> ret) {
    ret.putAll(authStrategy.resolvePermissions(dto, contextVariables));
  }
}
