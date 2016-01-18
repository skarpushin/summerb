package org.summerb.approaches.security.api;

public interface CurrentUserResolver<TUser> {
	/**
	 * Get current user (under which user context we're executing)
	 * 
	 * @return Current user object
	 * @throws CurrentUserNotFoundException
	 *             in case there is no current user. Using unchecked exception
	 *             intentionally since it's unexpected exception more like
	 *             RuntimeException
	 */
	TUser getUser() throws CurrentUserNotFoundException;

	String getUserUuid() throws CurrentUserNotFoundException;
}
