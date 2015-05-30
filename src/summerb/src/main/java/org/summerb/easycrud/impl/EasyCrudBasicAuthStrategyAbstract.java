package org.summerb.easycrud.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.easycrud.api.EasyCrudBasicAuthStrategy;
import org.summerb.security.api.SecurityContextResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;

public abstract class EasyCrudBasicAuthStrategyAbstract<TDto> implements EasyCrudBasicAuthStrategy<TDto> {
	@Autowired
	protected SecurityContextResolver securityContextResolver;

	protected UserDetails getUser() {
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
