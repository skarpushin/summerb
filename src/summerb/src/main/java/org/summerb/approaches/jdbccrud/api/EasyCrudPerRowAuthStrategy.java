package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing operations on per-row basis.
 * 
 * Normally injected into {@link EasyCrudService} via {@link EasyCrudWireTap},
 * but also can be used separately.
 * 
 * In case you do not need that detailed authorization rows you can use
 * {@link EasyCrudTableAuthStrategy}
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudPerRowAuthStrategy<TDto> {
	void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException;

	void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion) throws NotAuthorizedException;

	void assertAuthorizedToRead(TDto dto) throws NotAuthorizedException;

	void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException;
}
