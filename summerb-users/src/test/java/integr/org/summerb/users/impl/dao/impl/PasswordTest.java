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
package integr.org.summerb.users.impl.dao.impl;

import static org.junit.jupiter.api.Assertions.*;

import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.users.impl.config.UserServicesTestConfig;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, UserServicesTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class PasswordTest {

  @Autowired private PasswordService passwordService;

  @Autowired private UserService userService;

  @Test
  public void testSetUserPassword_expectWillBePerformedOk() throws Exception {
    User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

    String pwd1 = "aaaa";
    passwordService.setUserPassword(createdUser.getUuid(), pwd1);

    boolean result = passwordService.isUserPasswordValid(createdUser.getUuid(), pwd1);
    assertTrue(result);
  }

  @Test
  public void testSetUserPassword_expectDuplicateWillBeRewrittenWithoutErrors() throws Exception {
    User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

    String pwd1 = "aaaa";
    passwordService.setUserPassword(createdUser.getUuid(), pwd1);

    String pwd2 = "bbbb";
    passwordService.setUserPassword(createdUser.getUuid(), pwd2);

    boolean result = passwordService.isUserPasswordValid(createdUser.getUuid(), pwd2);
    assertTrue(result);
  }

  @Test
  public void testCreateRestorationToken_expectDuplicateWillBeRewrittenWithoutErrors()
      throws Exception {
    User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

    String pwd1 = "aaaa";
    passwordService.setUserPassword(createdUser.getUuid(), pwd1);

    String restorationToken = passwordService.getNewRestorationTokenForUser(createdUser.getUuid());

    boolean result =
        passwordService.isRestorationTokenValid(createdUser.getUuid(), restorationToken);
    assertTrue(result);

    passwordService.deleteRestorationToken(createdUser.getUuid());

    result = passwordService.isRestorationTokenValid(createdUser.getUuid(), restorationToken);
    assertFalse(result);
  }
}
