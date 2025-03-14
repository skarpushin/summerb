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

import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.validation.ValidationException;

// TODO: Re-do to use EasyCrud

/**
 * Service for managing user accounts. That is. This is just a user registry.
 *
 * <p>Authorization is out of the scope bacause it is very domain specific and is to be implemented
 * by final application
 *
 * @author skarpushin
 *     <p>TBD: Unify tables naming to have common prefix, like "users_"
 */
public interface UserService {
  User createUser(User user);

  User getUserByUuid(String userUuid) throws UserNotFoundException;

  User getUserByEmail(String userEmail) throws ValidationException, UserNotFoundException;

  PaginatedList<User> findUsersByDisplayNamePartial(
      String displayNamePartial, PagerParams pagerParams);

  void updateUser(User user) throws ValidationException, UserNotFoundException;

  void deleteUserByUuid(String userUuid) throws UserNotFoundException;
}
