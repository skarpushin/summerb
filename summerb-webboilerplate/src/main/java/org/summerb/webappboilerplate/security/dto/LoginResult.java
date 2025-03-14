/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.webappboilerplate.security.dto;

import java.util.Map;
import org.summerb.users.api.dto.User;
import org.summerb.utils.DtoBase;

public class LoginResult implements DtoBase {
  private static final long serialVersionUID = 252775263464590406L;

  public static final String ATTR_REDIRECT_TO = "redirectTo";

  private User user;
  private String rememberMeToken;

  /** Customer attributes */
  private Map<String, Object> attributes;

  public LoginResult() {}

  public LoginResult(User user, String rememberMeToken) {
    this.rememberMeToken = rememberMeToken;
    this.user = user;
  }

  public String getRememberMeToken() {
    return rememberMeToken;
  }

  public void setRememberMeToken(String authToken) {
    this.rememberMeToken = authToken;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Object> tags) {
    this.attributes = tags;
  }
}
