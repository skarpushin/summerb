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
package org.summerb.easycrud.impl.wireTaps;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

import com.google.common.base.Preconditions;

/**
 * WireTap which will invoke injected {@link EasyCrudTableAuthStrategy} when
 * accessing or modifying data.
 * 
 * <p>
 * 
 * It doesn't require full dto thus it doesn't prevent {@link EasyCrudService}
 * from performing batch operations.
 * 
 * @author sergeyk
 */
public class EasyCrudWireTapTableAuthImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EasyCrudTableAuthStrategy strategy;

	public EasyCrudWireTapTableAuthImpl(EasyCrudTableAuthStrategy strategy) {
		Preconditions.checkArgument(strategy != null);
		this.strategy = strategy;
	}

	@Override
	public boolean requiresFullDto() {
		return false;
	}

	@Override
	public boolean requiresOnCreate() throws ValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToCreate();
		return false;
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, ValidationException {
		strategy.assertAuthorizedToUpdate();
		return false;
	}

	@Override
	public boolean requiresOnDelete() throws ValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToDelete();
		return false;
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, ValidationException {
		strategy.assertAuthorizedToRead();
		return false;
	}

}
