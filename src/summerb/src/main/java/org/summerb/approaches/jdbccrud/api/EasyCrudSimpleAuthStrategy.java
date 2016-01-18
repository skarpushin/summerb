package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudSimpleAuthStrategy {
	void assertAuthorizedToCreate() throws NotAuthorizedException;

	void assertAuthorizedToUpdate() throws NotAuthorizedException;

	void assertAuthorizedToRead() throws NotAuthorizedException;

	void assertAuthorizedToDelete() throws NotAuthorizedException;
}
