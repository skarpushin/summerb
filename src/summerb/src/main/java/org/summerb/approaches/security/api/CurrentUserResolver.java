package org.summerb.approaches.security.api;

import org.summerb.microservices.users.api.dto.User;

/**
 * Interface for resolving the user from current context.
 * 
 * TODO: Why TUser doesn't extend {@link User}?? Either fix or clarify.
 * 
 * @author sergeyk
 *
 * @param <TUser>
 *            user DTO type
 */
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
