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
package org.summerb.webappboilerplate.security.impls;

import javax.servlet.http.HttpServletRequest;

import org.summerb.webappboilerplate.security.apis.RememberMeRequestedStrategy;
import org.summerb.webappboilerplate.security.dto.LoginParams;

/**
 * Our way - how to determine if remember me is requested
 *
 * @author sergeyk
 */
public class RememberMeRequestedStrategyImpl implements RememberMeRequestedStrategy {
  private String rememberMeParameter = "_spring_security_remember_me";

  @Override
  public boolean isRememberMeRequested(HttpServletRequest request) {
    boolean isJson =
        request.getContentType() != null && request.getContentType().contains("application/json");
    if (isJson) {
      return Boolean.TRUE.toString().equals(request.getHeader(LoginParams.HEADER_REMEMBER_ME));
    }

    return isFormAuthRequestForRememberMe(request);
  }

  private boolean isFormAuthRequestForRememberMe(HttpServletRequest request) {
    String paramValue = request.getParameter(rememberMeParameter);

    if (paramValue != null) {
      if (paramValue.equalsIgnoreCase("true")
          || paramValue.equalsIgnoreCase("on")
          || paramValue.equalsIgnoreCase("yes")
          || paramValue.equals("1")) {
        return true;
      }
    }

    return false;
  }

  public String getRememberMeParameter() {
    return rememberMeParameter;
  }

  public void setRememberMeParameter(String rememberMeParameter) {
    this.rememberMeParameter = rememberMeParameter;
  }
}
