/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.webappboilerplate.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class JsonResponseWriterGsonImpl implements JsonResponseWriter {
  protected Gson gson;

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
