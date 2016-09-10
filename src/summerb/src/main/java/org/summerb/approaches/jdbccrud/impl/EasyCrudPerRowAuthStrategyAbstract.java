package org.summerb.approaches.jdbccrud.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.jdbccrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

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
