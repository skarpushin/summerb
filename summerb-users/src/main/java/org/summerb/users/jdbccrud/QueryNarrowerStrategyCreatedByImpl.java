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

import org.summerb.easycrud.api.dto.HasAuthor;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.querynarrower.QueryNarrowerStrategyFieldBased;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.users.api.dto.User;

/**
 * This narrower simply trims query to only those items which were created by current user
 *
 * <p>WARNING: It's assummed dto implemented interface {@link HasAuthor}
 */
public class QueryNarrowerStrategyCreatedByImpl<TUser extends User>
    extends QueryNarrowerStrategyFieldBased {
  private SecurityContextResolver<TUser> securityContextResolver;

  public QueryNarrowerStrategyCreatedByImpl(
      SecurityContextResolver<TUser> securityContextResolver) {
    super(HasAuthor.FN_CREATED_BY);
    this.securityContextResolver = securityContextResolver;
  }

  @Override
  protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
    ret.eq(HasAuthor.FN_CREATED_BY, securityContextResolver.getUserUuid());
    return ret;
  }
}
