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
package org.summerb.spring.security.elevation;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.summerb.security.elevation.api.ElevationStrategy;
import org.summerb.spring.security.auth.BackgroundProcessAuthentication;

/**
 * This impl elevates to BackgroundProcess credentials if current user is
 * missing or ANonymous
 * 
 * @author sergey.karpushin
 *
 */
public class ElevationStrategyBkgProcessImpl implements ElevationStrategy {
	private BackgroundProcessAuthentication auth = new BackgroundProcessAuthentication("FallbackToBkgAuthProxy");

	@Override
	public boolean isElevationRequired() {
		return SecurityContextHolder.getContext() == null
				|| SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
	}

	@Override
	public Object elevate() {
		SecurityContext ret = SecurityContextHolder.getContext();
		SecurityContext context = new SecurityContextImpl();
		// NOTE: We're creating new instance of BackgroundProcessAuthentication
		// because there is setAuthenticated(false) possible
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
		return ret;
	}

	@Override
	public void deElevate(Object previousContext) {
		if (previousContext == null) {
			SecurityContextHolder.clearContext();
		} else {
			SecurityContextHolder.setContext((SecurityContext) previousContext);
		}
	}

}
