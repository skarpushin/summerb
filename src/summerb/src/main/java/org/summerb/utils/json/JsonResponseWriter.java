package org.summerb.utils.json;

import javax.servlet.http.HttpServletResponse;

public interface JsonResponseWriter {

	void writeResponseBody(Object dto, HttpServletResponse response);

}