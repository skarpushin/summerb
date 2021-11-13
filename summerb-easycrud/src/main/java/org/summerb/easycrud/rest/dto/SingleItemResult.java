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
package org.summerb.easycrud.rest.dto;

import java.util.Map;

import org.summerb.easycrud.api.dto.HasId;

public class SingleItemResult<TId, TDto extends HasId<TId>> extends CrudQueryResult<TId, TDto> {
	private TDto row;
	private Map<String, Boolean> permissions;

	public SingleItemResult() {
	}

	public SingleItemResult(String entityMessageCode, TDto row) {
		this.entityMessageCode = entityMessageCode;
		this.row = row;
	}

	public TDto getRow() {
		return row;
	}

	public void setRow(TDto row) {
		this.row = row;
	}

	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions = permissions;
	}

}
