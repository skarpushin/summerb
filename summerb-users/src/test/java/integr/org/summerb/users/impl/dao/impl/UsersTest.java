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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, UserServicesTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class UsersTest {

  @Autowired private UserService userService;

  @Test
  public void testCreateUser_expectUserWIllBeFoundByIdAfterCreation() throws Exception {
    User userToCreate = UserFactory.createNewUserTemplate();
    userToCreate = userService.createUser(userToCreate);

    User foundUser = userService.getUserByUuid(userToCreate.getUuid());
    assertNotNull(foundUser);

    assertEquals(userToCreate.getDisplayName(), foundUser.getDisplayName());
    assertEquals(userToCreate.getEmail(), foundUser.getEmail());
    assertEquals(userToCreate.getIntegrationData(), foundUser.getIntegrationData());
    assertEquals(userToCreate.getIsBlocked(), foundUser.getIsBlocked());
    assertEquals(userToCreate.getLocale(), foundUser.getLocale());
    assertEquals(userToCreate.getRegisteredAt(), foundUser.getRegisteredAt());
    assertEquals(userToCreate.getTimeZone(), foundUser.getTimeZone());
    assertEquals(userToCreate.getUuid(), foundUser.getUuid());
  }

  @Test
  public void testCreateUser_expectUserWIllBeFoundByEmailAfterCreation() throws Exception {
    User userToCreate = UserFactory.createNewUserTemplate();
    userToCreate = userService.createUser(userToCreate);

    User foundUser = userService.getUserByEmail(userToCreate.getEmail());
    assertNotNull(foundUser);
  }

  @Test
  public void testDeleteUser_expectUserWillNotBeFoundAfterDeletion() throws Exception {
    User userToCreate = UserFactory.createNewUserTemplate();
    userToCreate = userService.createUser(userToCreate);
    userService.deleteUserByUuid(userToCreate.getUuid());

    try {
      userService.getUserByEmail(userToCreate.getEmail());
      fail();
    } catch (UserNotFoundException e) {
      // it's expected
    }
  }

  @Test
  public void testUpdateUser_expectChangedValueAfterUpdate() throws Exception {
    User userToCreate = UserFactory.createNewUserTemplate();

    userToCreate.setDisplayName("old display name");
    userToCreate = userService.createUser(userToCreate);

    userToCreate.setDisplayName("new display name");
    userService.updateUser(userToCreate);

    User foundUser = userService.getUserByUuid(userToCreate.getUuid());

    assertEquals(userToCreate.getDisplayName(), foundUser.getDisplayName());
  }

  @Test
  public void testFindUsersByDisplayNamePartial_expectUsersWillBeFound() {
    User user = UserFactory.createNewUserTemplate();
    user.setDisplayName("oneUHapqoiwez");
    user.setEmail("email1@aaa.ru");
    userService.createUser(user);

    user = UserFactory.createNewUserTemplate();
    user.setDisplayName("twoUHapqoiwez");
    user.setEmail("email2@aaa.ru");
    userService.createUser(user);

    user = UserFactory.createNewUserTemplate();
    user.setDisplayName("threeUHapqoiwez");
    user.setEmail("email3@aaa.ru");
    userService.createUser(user);

    user = UserFactory.createNewUserTemplate();
    user.setDisplayName("other");
    user.setEmail("other@aaa.ru");
    userService.createUser(user);

    PaginatedList<User> results =
        userService.findUsersByDisplayNamePartial("UHapqoiwez", new PagerParams());

    assertNotNull(results);
    assertNotNull(results.getItems());
    assertEquals(3, results.getItems().size());
    assertEquals(3, results.getTotalResults());

    results = userService.findUsersByDisplayNamePartial("eUHapqoiwez", new PagerParams());
    assertEquals(2, results.getItems().size());
    assertEquals(2, results.getTotalResults());

    results = userService.findUsersByDisplayNamePartial("UHapqoiwez", new PagerParams(0, 1));
    assertEquals(1, results.getItems().size());
    assertEquals(3, results.getTotalResults());
  }

  @Test
  // @Rollback(false)
  public void testFindUsersByDisplayNamePartial_expectCorrectSorting() {
    User user = UserFactory.createNewUserTemplate();
    user.setDisplayName("ooUHapqoiwez");
    user.setEmail("email2@aaa.ru");
    userService.createUser(user);

    user = UserFactory.createNewUserTemplate();
    user.setDisplayName("oooUHapqoiwez");
    user.setEmail("email3@aaa.ru");
    userService.createUser(user);

    user = UserFactory.createNewUserTemplate();
    user.setDisplayName("oUHapqoiwez");
    user.setEmail("email1@aaa.ru");
    userService.createUser(user);

    PaginatedList<User> results =
        userService.findUsersByDisplayNamePartial("UHapqoiwez", new PagerParams());

    assertNotNull(results);
    List<User> items = results.getItems();
    assertNotNull(items);
    assertEquals(3, items.size());
    assertEquals(3, results.getTotalResults());

    assertEquals("email1@aaa.ru", items.get(0).getEmail());
    assertEquals("email2@aaa.ru", items.get(1).getEmail());
    assertEquals("email3@aaa.ru", items.get(2).getEmail());
  }
}
