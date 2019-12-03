/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;

/**
 * WireTap which will invoke injected {@link EasyCrudPerRowAuthStrategy} whn
 * accessing or modifying data.
 * 
 * @author sergeyk
 */
public class EasyCrudWireTapPerRowAuthImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EasyCrudPerRowAuthStrategy<TDto> strategy;

	public EasyCrudWireTapPerRowAuthImpl(EasyCrudPerRowAuthStrategy<TDto> strategy) {
		Preconditions.checkArgument(strategy != null);
		this.strategy = strategy;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToCreate(dto);
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToUpdate(from, to);
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToDelete(dto);
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToRead(dto);
	}

}
