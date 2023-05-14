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

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.rest.commonpathvars.HasCommonPathVariable;
import org.summerb.easycrud.rest.commonpathvars.HasCommonPathVariables;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

/**
 * Impl of this interface should be able to resolve current user permissions for
 * particular DTO.
 * 
 * In most cases this interface will be implemented by the same class that
 * implements {@link EasyCrudPerRowAuthStrategy}.
 * 
 * @author sergeyk
 */
public interface PermissionsResolverPerRow<TId, TDto extends HasId<TId>> {

	/**
	 * @param optionalDto      could be null. In this case it means we need to
	 *                         provide table-wide permissions
	 * 
	 * @param contextVariables context variables which were resolved from
	 *                         {@link RequestMapping} based on Controller-wide
	 *                         {@link HasCommonPathVariable} and
	 *                         {@link HasCommonPathVariables} annotations.
	 * 
	 * @return pairs: permission - availability. For standard action constants see
	 *         {@link Permissions}
	 */
	Map<String, Boolean> resolvePermissions(TDto optionalDto, PathVariablesMap contextVariables);
}
