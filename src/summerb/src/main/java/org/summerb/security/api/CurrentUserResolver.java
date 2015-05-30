package org.summerb.security.api;

import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.security.api.exceptions.CurrentUserNotFoundException;

public interface CurrentUserResolver {
	/**
	 * Get current user (under which user context we're executing)
	 * 
	 * @return Current user object
	 * @throws CurrentUserNotFoundException
	 *             in case there is no current user. Using unchecked exception
	 *             intentionally since it's unexpected exception more like
	 *             RuntimeException
	 */
	UserDetails getUser() throws CurrentUserNotFoundException;

	String getUserUuid() throws CurrentUserNotFoundException;
}
