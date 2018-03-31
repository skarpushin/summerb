package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing table-wide operation. Used in case when all rows
 * have same authorization rules.
 * 
 * <p>
 * 
 * Normally injected into {@link EasyCrudService} via {@link EasyCrudWireTap}
 * (particularly {@link EasyCrudWireTapPerRowAuthImpl} ), but also can be used
 * separately.
 * 
 * <p>
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
