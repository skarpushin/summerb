/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.spring.security.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.summerb.spring.security.SecurityConstants;

public class BackgroundProcessAuthentication implements Authentication {
	private static final long serialVersionUID = 3710514197842955814L;

	private String origin;

	private static List<? extends GrantedAuthority> AUTHORITIES = Arrays
			.asList(new SimpleGrantedAuthority(SecurityConstants.ROLE_BACKGROUND_PROCESS));
	private boolean authenticated = true;

	/**
	 * 
	 * @param origin some string which is probably suppose to clarify what is the
	 *               origin of that authentication. Not used for any logic - just
	 *               for tracing/debugging purposes
	 */
	public BackgroundProcessAuthentication(String origin) {
		this.origin = origin;
	}

	@Override
	public String getName() {
		return origin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AUTHORITIES;
	}

	@Override
	public Object getCredentials() {
		return origin;
	}

	@Override
	public Object getDetails() {
		return origin;
	}

	@Override
	public Object getPrincipal() {
		return origin;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new IllegalStateException("Opearion is not supported");
	}

}
