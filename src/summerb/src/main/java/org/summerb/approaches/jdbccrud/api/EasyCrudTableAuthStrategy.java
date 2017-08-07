package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing table-wide operation. Used in case when all rows
 * have same authorization rules.
 * 
 * Normally injected into {@link EasyCrudService} via {@link EasyCrudWireTap},
 * but also can be used separately.
 * 
 * In case you need per-row authorization rules use
 * {@link EasyCrudPerRowAuthStrategy}.
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudTableAuthStrategy {
	void assertAuthorizedToCreate() throws NotAuthorizedException;

	void assertAuthorizedToUpdate() throws NotAuthorizedException;

	void assertAuthorizedToRead() throws NotAuthorizedException;

	void assertAuthorizedToDelete() throws NotAuthorizedException;
}
