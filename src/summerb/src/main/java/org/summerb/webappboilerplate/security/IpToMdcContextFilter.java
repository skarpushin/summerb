package org.summerb.webappboilerplate.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class IpToMdcContextFilter extends OncePerRequestFilter {
	public static final String CLIENT_IP = "remoteAddr";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		MDC.put(CLIENT_IP, resolveRemoteAddr(request));
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
