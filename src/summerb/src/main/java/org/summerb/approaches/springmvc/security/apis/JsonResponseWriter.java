package org.summerb.approaches.springmvc.security.apis;

import javax.servlet.http.HttpServletResponse;

public interface JsonResponseWriter {

	void writeResponseBody(Object dto, HttpServletResponse response);

}