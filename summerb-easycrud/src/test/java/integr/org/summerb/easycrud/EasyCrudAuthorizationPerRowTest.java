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
package integr.org.summerb.easycrud;

import static integr.org.summerb.easycrud.QueryTest.buildRow;
import static org.junit.jupiter.api.Assertions.*;

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.testbeans.UserRowService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.auth.EasyCrudAuthorizationPerRow;
import org.summerb.easycrud.auth.EasyCrudAuthorizationPerRowAbstract;
import org.summerb.easycrud.auth.EasyCrudAuthorizationPerRowWireTapAdapter;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.easycrud.scaffold.EasyCrudScaffold;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class EasyCrudAuthorizationPerRowTest {
  @Autowired private EasyCrudScaffold easyCrudScaffold;
  @Autowired UserRowService userRowService;
  @Autowired CurrentUserUuidResolver currentUserUuidResolver;

  @Test
  public void testAuthorizationExceptionsWouldBeThrown() {
    UserRow row1 = userRowService.create(buildRow("env1", 30));
    UserRow row2 = userRowService.create(buildRow("env2", 20));
    UserRow row3 = userRowService.create(buildRow("env3", 10));

    EasyCrudAuthorizationPerRow<UserRow> auth =
        new EasyCrudAuthorizationPerRowAbstract<>() {
          @Override
          protected boolean isAllowedToModify(UserRow row) {
            return false;
          }

          @Override
          public boolean isAllowedToRead(List<UserRow> rows) {
            return false;
          }
        };

    // NOTE: Here we're wrapping EasyCrudAuthorizationPerRow into
    // EasyCrudAuthorizationPerRowWireTapAdapter ourselves
    EasyCrudAuthorizationPerRowWireTapAdapter<UserRow> wireTap =
        new EasyCrudAuthorizationPerRowWireTapAdapter<>(
            UserRowService.TERM, currentUserUuidResolver, auth);
    UserRowService service =
        easyCrudScaffold.fromService(
            UserRowService.class, UserRowService.TERM, "users_table", wireTap);

    try {
      service.create(buildRow("envX", 30));
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.CREATE, err.getOperationMessageCode());
      assertEquals(UserRowService.TERM + ":new_row", err.getSubjectTitle());
    }

    try {
      service.getById(row1.getId());
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.READ, err.getOperationMessageCode());
      assertEquals(UserRowService.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service.query().ge(UserRow::getKarma, 20).findAll(service.orderBy(UserRow::getKarma).asc());
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.READ, err.getOperationMessageCode());
      assertEquals(
          UserRowService.TERM + ":" + row2.getId() + "," + row1.getId(), err.getSubjectTitle());
    }

    try {
      row1.setName("env" + System.currentTimeMillis());
      service.update(row1);
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.UPDATE, err.getOperationMessageCode());
      assertEquals(UserRowService.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service.delete(row1);
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.DELETE, err.getOperationMessageCode());
      assertEquals(UserRowService.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service.deleteByQuery(service.query().ge(UserRow::getKarma, 30));
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.DELETE, err.getOperationMessageCode());
      assertEquals(UserRowService.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }
  }

  @Test
  public void testAuthorizationMethodsAreInvoked() {
    UserRow row1 = buildRow("env1", 30);
    String initialUuid = UUID.randomUUID().toString();
    row1.setAbout(initialUuid);
    row1 = userRowService.create(row1);

    UserRow row2 = userRowService.create(buildRow("env2", 20));
    UserRow row3 = userRowService.create(buildRow("env3", 10));

    List<UserRow> isAllowedToCreate = new ArrayList<>();
    List<List<UserRow>> isAllowedToRead = new ArrayList<>();
    List<UserRow> isAllowedToUpdateCurrent = new ArrayList<>();
    List<UserRow> isAllowedToUpdate = new ArrayList<>();
    List<List<UserRow>> isAllowedToDelete = new ArrayList<>();

    EasyCrudAuthorizationPerRow<UserRow> auth =
        new EasyCrudAuthorizationPerRow<>() {

          @Override
          public boolean isAllowedToCreate(UserRow row) {
            isAllowedToCreate.add(row);
            return true;
          }

          @Override
          public boolean isAllowedToRead(List<UserRow> rows) {
            isAllowedToRead.add(rows);
            return true;
          }

          @Override
          public boolean isAllowedToUpdate(UserRow currentVersion, UserRow newVersion) {
            isAllowedToUpdateCurrent.add(currentVersion);
            isAllowedToUpdate.add(newVersion);
            return true;
          }

          @Override
          public boolean isCurrentVersionNeededForUpdatePermissionCheck() {
            return true;
          }

          @Override
          public boolean isAllowedToDelete(List<UserRow> rows) {
            isAllowedToDelete.add(rows);
            return true;
          }
        };

    // NOTE: Here we're injecting just instance of interface EasyCrudAuthorizationPerRow, expecting
    // easyCrudScaffold to wrap this in EasyCrudAuthorizationPerRowWireTapAdapter
    UserRowService service =
        easyCrudScaffold.fromService(
            UserRowService.class, UserRowService.TERM, "users_table", auth);

    // create
    UserRow row4 = service.create(buildRow("envX", 40));
    assertEquals(1, isAllowedToCreate.size());
    assertEquals("envX", isAllowedToCreate.get(0).getName());

    // read one
    service.getById(row1.getId());
    assertEquals(1, isAllowedToRead.size());
    assertEquals(1, isAllowedToRead.get(0).size());
    assertEquals("env1", isAllowedToRead.get(0).get(0).getName());

    // read many
    service.query().ge(UserRow::getKarma, 30).findAll(service.orderBy(UserRow::getKarma).asc());
    assertEquals(2, isAllowedToRead.size());
    assertEquals(2, isAllowedToRead.get(1).size());
    assertEquals("env1", isAllowedToRead.get(1).get(0).getName());
    assertEquals("envX", isAllowedToRead.get(1).get(1).getName());

    // update
    String newUuid = UUID.randomUUID().toString();
    row1.setAbout(newUuid);
    row1 = service.update(row1);
    assertEquals(1, isAllowedToUpdateCurrent.size());
    assertEquals(initialUuid, isAllowedToUpdateCurrent.get(0).getAbout());
    assertEquals(1, isAllowedToUpdate.size());
    assertEquals(newUuid, isAllowedToUpdate.get(0).getAbout());

    // delete single
    service.delete(row1);
    assertEquals(1, isAllowedToDelete.size());
    assertEquals(1, isAllowedToDelete.get(0).size());
    assertEquals("env1", isAllowedToDelete.get(0).get(0).getName());

    // delete single
    service.deleteByQuery(service.query().ge(UserRow::getKarma, 20));
    assertEquals(2, isAllowedToDelete.size());
    assertEquals(2, isAllowedToDelete.get(1).size());
    assertTrue(isAllowedToDelete.get(1).stream().anyMatch(x -> x.getName().equals("env2")));
    assertTrue(isAllowedToDelete.get(1).stream().anyMatch(x -> x.getName().equals("envX")));
  }
}
