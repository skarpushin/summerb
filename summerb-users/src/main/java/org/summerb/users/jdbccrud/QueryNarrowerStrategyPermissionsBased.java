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
package org.summerb.users.jdbccrud;

import java.util.List;
import java.util.stream.Collectors;

import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.querynarrower.QueryNarrowerStrategyFieldBased;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.users.api.PermissionService;

/**
 * Default impl for narrower that finds objects user permitted to access.
 *
 * <p>WARNING: DO NOT USE it if you anticipate MANY objects of this type per user. As no pagination
 * is implemented here -- all ids retrieved at once
 */
public class QueryNarrowerStrategyPermissionsBased<TRow extends HasId<?>>
    extends QueryNarrowerStrategyFieldBased<TRow> {
  protected PermissionService permissionService;
  protected String optionalDomain;
  protected String optionalRequiredPermission;
  protected CurrentUserUuidResolver currentUserUuidResolver;
  protected Class<?> idClass;

  public QueryNarrowerStrategyPermissionsBased(
      Class<TRow> rowClass,
      PermissionService permissionService,
      CurrentUserUuidResolver currentUserUuidResolver,
      Class<?> idClass,
      String optionalDomain,
      String optionalRequiredPermission) {
    super(rowClass, HasId.FN_ID);
    this.permissionService = permissionService;
    this.currentUserUuidResolver = currentUserUuidResolver;
    this.idClass = idClass;
    this.optionalDomain = optionalDomain;
    this.optionalRequiredPermission = optionalRequiredPermission;
  }

  @Override
  protected Query<TRow> doNarrow(Query<TRow> ret, PathVariablesMap allRequestParams) {
    List<String> idsStrs =
        permissionService.findSubjectsUserHasPermissionsFor(
            optionalDomain, currentUserUuidResolver.getUserUuid(), optionalRequiredPermission);

    if (idClass.equals(String.class)) {
      if (idsStrs.size() == 0) {
        ret.eq(HasId.FN_ID, "NA");
      } else {
        ret.in(HasId.FN_ID, idsStrs);
      }
    } else {
      if (idsStrs.size() == 0) {
        // NOTE: Yes potentially there could be item with id = 0,
        // but still it's better to try that one instead of all
        // environments
        ret.eq(HasId.FN_ID, 0);
      } else {
        List<Long> idsLongs =
            idsStrs.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        ret.in(HasId.FN_ID, idsLongs);
      }
    }

    return ret;
  }
}
