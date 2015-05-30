package org.summerb.easycrud.api;

import org.summerb.security.api.exceptions.NotAuthorizedException;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface EasyCrudSimpleAuthStrategy {
	void assertAuthorizedToCreate() throws NotAuthorizedException;

	void assertAuthorizedToUpdate() throws NotAuthorizedException;

	void assertAuthorizedToRead() throws NotAuthorizedException;

	void assertAuthorizedToDelete() throws NotAuthorizedException;
}
