package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * This auth strategy applies to situations when authorization happens on
 * object-based level.
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudPerRowAuthStrategy<TDto> {
	void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException;

	void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion) throws NotAuthorizedException;

	void assertAuthorizedToRead(TDto dto) throws NotAuthorizedException;

	void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException;
}
