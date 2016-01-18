package org.summerb.approaches.jdbccrud.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudBasicAuthStrategy;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * @deprecated Use for testing purposes only
 * @author sergeyk
 *
 */
@Deprecated
public class EasyCrudBasicAuthStrategyNoOpImpl<TDto> implements EasyCrudBasicAuthStrategy<TDto> {

	@Override
	public void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException {
	}

	@Override
	public void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion) throws NotAuthorizedException {
	}

	@Override
	public void assertAuthorizedToRead(TDto dto) throws NotAuthorizedException {
	}

	@Override
	public void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException {
	}

}
