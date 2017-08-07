package org.summerb.approaches.springmvc.utils;

/**
 * Impl of this interface suppose to build external url (full url, including
 * http, port, context path).
 * 
 * MUST NOT contain trailing slash
 * 
 * @author sergeyk
 *
 */
public interface AbsoluteUrlBuilder {
	String buildExternalUrl(String optionalRelativeUrl);
}
