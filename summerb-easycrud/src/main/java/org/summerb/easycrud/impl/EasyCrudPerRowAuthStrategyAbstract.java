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
package org.summerb.easycrud.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.spring.security.api.SecurityContextResolver;

public abstract class EasyCrudPerRowAuthStrategyAbstract<TDto, TUserType> implements EasyCrudPerRowAuthStrategy<TDto> {
	@Autowired
	protected SecurityContextResolver<TUserType> securityContextResolver;

	protected TUserType getUser() {
		return securityContextResolver.getUser();
	}

	@Override
	public void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException {
		assertAuthorizedToModify(dto);
	}

	protected abstract void assertAuthorizedToModify(TDto dto) throws NotAuthorizedException;

	@Override
	public void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion) throws NotAuthorizedException {
		assertAuthorizedToModify(newVersion);
	}

	@Override
	public void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException {
		assertAuthorizedToModify(dto);
	}

}
