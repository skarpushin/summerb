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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.summerb.i18n.exceptions.dto.ExceptionInfo;
import org.summerb.i18n.exceptions.dto.GenericServerErrorResult;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.validation.ValidationErrors;
import org.summerb.validation.ValidationException;
import org.summerb.webappboilerplate.utils.exceptions.translator.ExceptionTranslator;
import org.summerb.webappboilerplate.utils.json.JsonResponseWriter;
import org.summerb.webappboilerplate.utils.json.JsonResponseWriterGsonImpl;

public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
  protected JsonResponseWriter jsonResponseHelper;
  protected ExceptionTranslator exceptionTranslator;

  public RestAuthenticationFailureHandler() {
    jsonResponseHelper = new JsonResponseWriterGsonImpl();
  }

  public RestAuthenticationFailureHandler(JsonResponseWriter jsonResponseHelper) {
    this.jsonResponseHelper = jsonResponseHelper;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

    ValidationException fve =
        ExceptionUtils.findExceptionOfType(exception, ValidationException.class);
    if (fve != null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      jsonResponseHelper.writeResponseBody(new ValidationErrors(fve.getErrors()), response);
      return;
    }

    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    GenericServerErrorResult responseBody =
        new GenericServerErrorResult(
            exceptionTranslator.buildUserMessage(exception, LocaleContextHolder.getLocale()),
            new ExceptionInfo(exception));
    jsonResponseHelper.writeResponseBody(responseBody, response);
  }

  public ExceptionTranslator getExceptionTranslator() {
    return exceptionTranslator;
  }

  @Autowired
  public void setExceptionTranslator(ExceptionTranslator exceptionTranslator) {
    this.exceptionTranslator = exceptionTranslator;
  }
}
