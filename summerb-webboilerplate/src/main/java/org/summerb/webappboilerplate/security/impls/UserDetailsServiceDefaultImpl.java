/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.webappboilerplate.security.impls;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.validation.FieldValidationException;

/**
 * Proposed default impl for {@link UserDetailsService} that users
 * {@link UserService} and {@link PermissionService}
 * 
 * @author sergeyk
 *
 */
public class UserDetailsServiceDefaultImpl implements UserDetailsService {
	private UserService userService;
	private PermissionService permissionService;

	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		try {
			User user = userService.getUserByEmail(userEmail);

			List<String> permissions = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN,
					user.getUuid(), null);

			AuthToken authToken = null;

			UserDetailsImpl ret = new UserDetailsImpl(user, null, permissions, authToken);
			return ret;
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException("User not found", e);
		} catch (FieldValidationException e) {
			throw new UsernameNotFoundException("Email provided in invalid format", e);
		} catch (Throwable t) {
			throw new UsernameNotFoundException("Failed to get user by email", t);
		}
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

}
