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
package org.summerb.easycrud.impl.wireTaps;

import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

public class EasyCrudWireTapNoOpImpl<TId, TDto extends HasId<TId>> implements EasyCrudWireTap<TId, TDto> {

	@Override
	public boolean requiresFullDto() {
		return true;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return false;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return false;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return false;
	}

	@Override
	public void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return false;
	}

	@Override
	public void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

}
