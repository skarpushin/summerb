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
package integr.org.summerb.users.impl.dao.impl;

import static org.junit.jupiter.api.Assertions.*;

import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.users.impl.config.UserServicesTestConfig;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.impl.dao.AuthTokenDao;
import org.summerb.users.impl.dao.PasswordDao;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, UserServicesTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class AuthTokenTest {
  @Autowired private UserService userService;

  @Autowired private PasswordService passwordService;

  @Autowired private AuthTokenService authTokenService;

  @Autowired private AuthTokenDao authTokenDao;

  @Autowired private PasswordDao passwordDao;

  @Test
  public void testCreateAuthToken_expectOk() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken);
  }

  @Test
  public void testIsAuthTokenValid_expectTokenMustBeValidRightAfterCreation() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken);

    AuthToken result =
        authTokenService.isAuthTokenValid(
            user.getUuid(), authToken.getUuid(), authToken.getTokenValue());
    assertNotNull(result);
  }

  @Test
  public void testIsAuthTokenValid_expectWillNotUpdateLastVerifiedForOldValue() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");
    AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken);

    authTokenDao.updateToken(authToken.getUuid(), 5, null);

    authToken = authTokenService.getAuthTokenByUuid(authToken.getUuid());

    assertTrue(authToken.getLastVerifiedAt() > 5);
  }

  @Test
  public void testUpdateToken_expectValueWillBeUpdated() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    AuthToken authToken =
        authTokenService.createAuthToken(user.getEmail(), "LOCAL", "tUuid1", "tValue1");
    assertNotNull(authToken);
    authTokenDao.updateToken(authToken.getUuid(), new Date().getTime() + 1, "newValue2");

    authToken = authTokenService.getAuthTokenByUuid(authToken.getUuid());
    assertEquals("newValue2", authToken.getTokenValue());
  }

  @Test
  public void testDeleteAuthToken_expectDeletedAuthTokenMustNotBeValid() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken);

    AuthToken result =
        authTokenService.isAuthTokenValid(
            user.getUuid(), authToken.getUuid(), authToken.getTokenValue());
    assertNotNull(result);

    authTokenService.deleteAuthToken(authToken.getUuid());
    result =
        authTokenService.isAuthTokenValid(
            user.getUuid(), authToken.getUuid(), authToken.getTokenValue());
    assertNull(result);
  }

  @Test
  public void testFindExpiredAuthTokens_expect2Tokens() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    AuthToken authToken1 = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken1);
    Thread.sleep(501);

    AuthToken authToken2 = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
    assertNotNull(authToken2);
    Thread.sleep(501);

    List<AuthToken> tokens = authTokenService.findUserAuthTokens(user.getUuid());
    assertEquals(2, tokens.size());
  }

  @Test
  public void testUpdateUserPassword_expectWillReportAffectedRecords() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    int result = passwordDao.updateUserPassword(user.getUuid(), "new-hash");
    assertTrue(result > 0);

    // do the same. Still expect affected > 0
    result = passwordDao.updateUserPassword(user.getUuid(), "new-hash");
    assertTrue(result > 0);
  }

  @Test
  public void testSetRestorationToken_expectWillReportAffectedRecords() throws Exception {
    User user = userService.createUser(UserFactory.createNewUserTemplate());
    passwordService.setUserPassword(user.getUuid(), "aaa");

    int result = passwordDao.setRestorationToken(user.getUuid(), "new-hash");
    assertTrue(result > 0);

    // do the same. Still expect affected > 0
    result = passwordDao.setRestorationToken(user.getUuid(), "new-hash");
    assertTrue(result > 0);
  }
}
