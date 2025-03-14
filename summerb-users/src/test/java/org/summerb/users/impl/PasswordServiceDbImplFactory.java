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
package org.summerb.users.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.PasswordFactory;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.impl.dao.PasswordDao;

@SuppressWarnings("deprecation")
public class PasswordServiceDbImplFactory {
  private PasswordServiceDbImplFactory() {}

  public static PasswordServiceImpl createPasswordServiceDbImpl() {
    UserService userService = UserServiceImplFactory.createUsersServiceImpl();
    PasswordDao passwordDao = Mockito.mock(PasswordDao.class);

    PasswordServiceImpl ret =
        new PasswordServiceImpl(passwordDao, new StandardPasswordEncoder("test"), userService);

    when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER))
        .thenReturn(PasswordFactory.createExistentUserPassword());
    when(passwordDao.findPasswordByUserUuid(UserFactory.NON_EXISTENT_USER)).thenReturn(null);
    when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD))
        .thenReturn(null);
    when(passwordDao.findPasswordByUserUuid(UserFactory.USER_RESULT_IN_EXCEPTION))
        .thenThrow(new IllegalStateException("Simulate unexpected excception"));
    when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER_2_PROBLEM_WITH_PASSWORD))
        .thenThrow(new IllegalStateException("Simulate unexpected excception"));

    when(passwordDao.updateUserPassword(eq(UserFactory.EXISTENT_USER), anyString())).thenReturn(1);
    when(passwordDao.updateUserPassword(
            eq(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD), anyString()))
        .thenReturn(0);

    when(passwordDao.setRestorationToken(eq(UserFactory.EXISTENT_USER), isNull())).thenReturn(1);
    when(passwordDao.setRestorationToken(eq(UserFactory.EXISTENT_USER), anyString())).thenReturn(1);
    when(passwordDao.setRestorationToken(
            eq(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD), anyString()))
        .thenReturn(0);

    return ret;
  }
}
