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
package org.summerb.webappboilerplate.security.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.session.InvalidSessionStrategy;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.webappboilerplate.utils.json.JsonResponseWriter;
import org.summerb.webappboilerplate.utils.json.JsonResponseWriterGsonImpl;

public class RestInvalidSessionStrategy implements InvalidSessionStrategy {
  protected JsonResponseWriter jsonResponseHelper;

  public RestInvalidSessionStrategy() {
    jsonResponseHelper = new JsonResponseWriterGsonImpl();
  }

  public RestInvalidSessionStrategy(JsonResponseWriter jsonResponseHelper) {
    this.jsonResponseHelper = jsonResponseHelper;
  }

  @Override
  public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // create new session, which will result in JSESSIONID coockie reset
    request.getSession();

    // Report that session changed and need to reestablish request
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    jsonResponseHelper.writeResponseBody(
        new NotAuthorizedResult("anonymous", SecurityMessageCodes.INVALID_SESSION), response);
  }
}
