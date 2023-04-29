/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

/**
 * Impl of this interface will be used to resolve permissions and communicate it
 * to consumer along with the data
 * 
 * @author sergeyk
 */
public interface PermissionsResolverStrategy<TId, TDto extends HasId<TId>> {
	/**
	 * @param contextVariables variables that defines current context that can be
	 *                         used for table-wide permissions resolution
	 */
	void resolvePermissions(MultipleItemsResult<TId, TDto> ret, PathVariablesMap contextVariables);

	void resolvePermissions(SingleItemResult<TId, TDto> ret);
}
