/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.webappboilerplate.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class IpToMdcContextFilter extends OncePerRequestFilter {
	public static final String CLIENT_IP = "remoteAddr";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ThreadContext.put(CLIENT_IP, resolveRemoteAddr(request));
		filterChain.doFilter(request, response);
	}

	private String resolveRemoteAddr(HttpServletRequest request) {
		String ret = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(ret)) {
			return ret;
		}

		return request.getRemoteAddr();
	}

}
