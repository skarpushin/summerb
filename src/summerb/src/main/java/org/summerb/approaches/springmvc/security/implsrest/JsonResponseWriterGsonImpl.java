package org.summerb.approaches.springmvc.security.implsrest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonResponseWriterGsonImpl implements JsonResponseWriter {
	private Gson gson;

	public JsonResponseWriterGsonImpl() {
		gson = new GsonBuilder().create();
	}

	public JsonResponseWriterGsonImpl(Gson gson) {
		this.gson = gson;
	}

	@Override
	public void writeResponseBody(Object dto, HttpServletResponse response) {
		try {
			String json = gson.toJson(dto);
			byte[] content = json.getBytes("UTF-8");
			response.setContentLength(content.length);
			response.setContentType("application/json;charset=UTF-8");
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(content);
			outputStream.flush();
		} catch (Exception exc) {
			throw new RuntimeException("Failed to write response body", exc);
		}
	}
}
