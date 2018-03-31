package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing operations on per-row basis. Which means that users
 * will have different access based on row data (or relevant data). Thus each
 * row that user is attempting to access needs to be checked.
 * 
 * <p>
 * 
 * Normally injected into {@link EasyCrudService} via
 * {@link EasyCrudWireTapPerRowAuthImpl}, but also can be used separately.
 * 
 * <p>
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
