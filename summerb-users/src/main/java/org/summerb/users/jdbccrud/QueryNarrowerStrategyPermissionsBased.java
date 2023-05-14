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

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.querynarrower.QueryNarrowerStrategyFieldBased;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.dto.User;

/**
 * Default impl for narrower that finds objects user permitted to access.
 * 
 * WARNING: DO NOT USE it if you anticipate MANY objects of this type per user.
 * As no pagination is implemented here -- all ids retrieved at once
 */
public class QueryNarrowerStrategyPermissionsBased<TUser extends User> extends QueryNarrowerStrategyFieldBased {
	private PermissionService permissionService;
	private String optionalDomain;
	private String optionalRequiredPermission;
	private SecurityContextResolver<TUser> securityContextResolver;
	private Class<?> idClass;

	public QueryNarrowerStrategyPermissionsBased(PermissionService permissionService,
			SecurityContextResolver<TUser> securityContextResolver, Class<?> idClass, String optionalDomain,
			String optionalRequiredPermission) {
		super(HasId.FN_ID);
		this.permissionService = permissionService;
		this.securityContextResolver = securityContextResolver;
		this.idClass = idClass;
		this.optionalDomain = optionalDomain;
		this.optionalRequiredPermission = optionalRequiredPermission;
	}

	@Override
	protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
		List<String> idsStrs = permissionService.findSubjectsUserHasPermissionsFor(optionalDomain,
				securityContextResolver.getUserUuid(), optionalRequiredPermission);

		if (idClass.equals(String.class)) {
			if (idsStrs.size() == 0) {
				ret.eq(HasId.FN_ID, "NA");
			} else {
				ret.in(HasId.FN_ID, idsStrs.toArray(new String[0]));
			}
		} else {
			if (idsStrs.size() == 0) {
				// NOTE: Yes potentially there could be item with id = 0,
				// but still it's better to try that one instead of all
				// environments
				ret.eq(HasId.FN_ID, 0);
			} else {
				List<Long> idsLongs = idsStrs.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
				ret.in(HasId.FN_ID, idsLongs.toArray(new Long[0]));
			}
		}

		return ret;
	}
}
