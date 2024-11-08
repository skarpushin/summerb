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
package org.summerb.users.impl;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Mockito;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.AuthTokenFactory;
import org.summerb.users.impl.dao.AuthTokenDao;
import org.summerb.utils.easycrud.api.dto.PagerParams;

public class AuthTokenServiceDbImplFactory {

  public static final PagerParams pagerParamsUnexpectedException = new PagerParams(7, 9);

  public static AuthTokenServiceImpl createAuthTokenServiceDbImpl() {
    AuthTokenDao authTokenDao = Mockito.mock(AuthTokenDao.class);

    AuthTokenServiceImpl ret =
        new AuthTokenServiceImpl(
            authTokenDao,
            UserServiceImplFactory.createUsersServiceImpl(),
            PasswordServiceDbImplFactory.createPasswordServiceDbImpl());

    when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXCEPTION))
        .thenThrow(new IllegalStateException("test simulate exception"));
    when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXISTENT))
        .thenReturn(AuthTokenFactory.createAuthTokenForExistentUser());
    when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT))
        .thenReturn(null);

    when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXPIRED))
        .thenReturn(AuthTokenFactory.createExpiredAuthToken());

    List<AuthToken> expiredTokens = new LinkedList<>();
    expiredTokens.add(AuthTokenFactory.createExpiredAuthToken());

    // PaginatedList<AuthToken> expiredAuthTokens = new PaginatedList<AuthToken>(new
    // PagerParams(), expiredTokens, 1);

    return ret;
  }
}
