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
package org.summerb.users.api.exceptions;

import org.summerb.i18n.HasMessageCode;

public class UserNotFoundException extends UsersServiceException implements HasMessageCode {
  private static final long serialVersionUID = 1899087866041906798L;

  public static final String ERROR_LOGIN_USER_NOT_FOUND = "error.login.userNotFound";

  private String userIdentifier;

  /**
   * @deprecated constructor exists only for IO purposes
   */
  @Deprecated
  public UserNotFoundException() {}

  public UserNotFoundException(String userIdentifier) {
    super("User not found: " + userIdentifier);
    this.userIdentifier = userIdentifier;
  }

  public String getUserIdentifier() {
    return userIdentifier;
  }

  @Override
  public String getMessageCode() {
    return ERROR_LOGIN_USER_NOT_FOUND;
  }
}
