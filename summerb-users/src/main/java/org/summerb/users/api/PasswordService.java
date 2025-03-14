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
package org.summerb.users.api;

import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.validation.ValidationException;

/**
 * This service manages user passwords and restoration tokens.
 *
 * <p>Passwords functionality defined separately from @see {@link UserService} because some
 * implementation can create and update passwords, some could be able to only verify user passwords.
 * For example if we will try to use ldap as auth provider we'll be able to validate, but unable to
 * modify user passwords
 *
 * @author skarpushin
 */
public interface PasswordService {
  boolean isUserPasswordValid(String userUuid, String passwordPlain) throws UserNotFoundException;

  void setUserPassword(String userUuid, String newPasswordPlain)
      throws UserNotFoundException, ValidationException;

  String getNewRestorationTokenForUser(String userUuid) throws UserNotFoundException;

  boolean isRestorationTokenValid(String userUuid, String restorationTokenUuid)
      throws UserNotFoundException;

  void deleteRestorationToken(String userUuid) throws UserNotFoundException;
}
