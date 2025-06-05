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

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.testbeans.TestDto1Service;
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
import org.summerb.easycrud.api.EasyCrudAuthorizationPerRow;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowAbstract;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowWireTapAdapter;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class EasyCrudAuthorizationPerRowTest {
  @Autowired private EasyCrudScaffold easyCrudScaffold;
  @Autowired TestDto1Service testDto1Service;
  @Autowired CurrentUserUuidResolver currentUserUuidResolver;

  @Test
  public void testAuthorizationExceptionsWouldBeThrown() {
    TestDto1 row1 = testDto1Service.create(buildRow("env1", 30));
    TestDto1 row2 = testDto1Service.create(buildRow("env2", 20));
    TestDto1 row3 = testDto1Service.create(buildRow("env3", 10));

    EasyCrudAuthorizationPerRow<TestDto1> auth =
        new EasyCrudAuthorizationPerRowAbstract<>() {
          @Override
          protected boolean isAllowedToModify(TestDto1 testDto1) {
            return false;
          }

          @Override
          public boolean isAllowedToRead(List<TestDto1> rows) {
            return false;
          }
        };

    // NOTE: Here we're wrapping EasyCrudAuthorizationPerRow into
    // EasyCrudAuthorizationPerRowWireTapAdapter ourselves
    EasyCrudAuthorizationPerRowWireTapAdapter<TestDto1> wireTap =
        new EasyCrudAuthorizationPerRowWireTapAdapter<>(
            TestDto1Service.TERM, currentUserUuidResolver, auth);
    TestDto1Service service =
        easyCrudScaffold.fromService(
            TestDto1Service.class, TestDto1Service.TERM, "forms_test_1", wireTap);

    try {
      service.create(buildRow("envX", 30));
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.CREATE, err.getOperationMessageCode());
      assertEquals(TestDto1Service.TERM + ":new_row", err.getSubjectTitle());
    }

    try {
      service.getById(row1.getId());
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.READ, err.getOperationMessageCode());
      assertEquals(TestDto1Service.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service
          .query()
          .ge(TestDto1::getMajorVersion, 20)
          .findAll(service.orderBy(TestDto1::getMajorVersion).asc());
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.READ, err.getOperationMessageCode());
      assertEquals(
          TestDto1Service.TERM + ":" + row2.getId() + "," + row1.getId(), err.getSubjectTitle());
    }

    try {
      row1.setLinkToFullDownload("" + System.currentTimeMillis());
      service.update(row1);
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.UPDATE, err.getOperationMessageCode());
      assertEquals(TestDto1Service.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service.delete(row1);
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.DELETE, err.getOperationMessageCode());
      assertEquals(TestDto1Service.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }

    try {
      service.deleteByQuery(service.query().ge(TestDto1::getMajorVersion, 30));
      fail("Suppose to throw exception");
    } catch (NotAuthorizedException nae) {
      NotAuthorizedResult err = nae.getErrorDescriptionObject();
      assertEquals("user1", err.getUserName());
      assertEquals(Permissions.DELETE, err.getOperationMessageCode());
      assertEquals(TestDto1Service.TERM + ":" + row1.getId(), err.getSubjectTitle());
    }
  }

  @Test
  public void testAuthorizationMethodsAreInvoked() {
    TestDto1 row1 = buildRow("env1", 30);
    String initialUuid = UUID.randomUUID().toString();
    row1.setLinkToFullDownload(initialUuid);
    row1 = testDto1Service.create(row1);

    TestDto1 row2 = testDto1Service.create(buildRow("env2", 20));
    TestDto1 row3 = testDto1Service.create(buildRow("env3", 10));

    List<TestDto1> isAllowedToCreate = new ArrayList<>();
    List<List<TestDto1>> isAllowedToRead = new ArrayList<>();
    List<TestDto1> isAllowedToUpdateCurrent = new ArrayList<>();
    List<TestDto1> isAllowedToUpdate = new ArrayList<>();
    List<List<TestDto1>> isAllowedToDelete = new ArrayList<>();

    EasyCrudAuthorizationPerRow<TestDto1> auth =
        new EasyCrudAuthorizationPerRow<>() {

          @Override
          public boolean isAllowedToCreate(TestDto1 row) {
            isAllowedToCreate.add(row);
            return true;
          }

          @Override
          public boolean isAllowedToRead(List<TestDto1> rows) {
            isAllowedToRead.add(rows);
            return true;
          }

          @Override
          public boolean isAllowedToUpdate(TestDto1 currentVersion, TestDto1 newVersion) {
            isAllowedToUpdateCurrent.add(currentVersion);
            isAllowedToUpdate.add(newVersion);
            return true;
          }

          @Override
          public boolean isCurrentVersionNeededForUpdatePermissionCheck() {
            return true;
          }

          @Override
          public boolean isAllowedToDelete(List<TestDto1> rows) {
            isAllowedToDelete.add(rows);
            return true;
          }
        };

    // NOTE: Here we're injecting just instance of interface EasyCrudAuthorizationPerRow, expecting
    // easyCrudScaffold to wrap this in EasyCrudAuthorizationPerRowWireTapAdapter
    TestDto1Service service =
        easyCrudScaffold.fromService(
            TestDto1Service.class, TestDto1Service.TERM, "forms_test_1", auth);

    // create
    TestDto1 row4 = service.create(buildRow("envX", 40));
    assertEquals(1, isAllowedToCreate.size());
    assertEquals("envX", isAllowedToCreate.get(0).getEnv());

    // read one
    service.getById(row1.getId());
    assertEquals(1, isAllowedToRead.size());
    assertEquals(1, isAllowedToRead.get(0).size());
    assertEquals("env1", isAllowedToRead.get(0).get(0).getEnv());

    // read many
    service
        .query()
        .ge(TestDto1::getMajorVersion, 30)
        .findAll(service.orderBy(TestDto1::getMajorVersion).asc());
    assertEquals(2, isAllowedToRead.size());
    assertEquals(2, isAllowedToRead.get(1).size());
    assertEquals("env1", isAllowedToRead.get(1).get(0).getEnv());
    assertEquals("envX", isAllowedToRead.get(1).get(1).getEnv());

    // update
    String newUuid = UUID.randomUUID().toString();
    row1.setLinkToFullDownload(newUuid);
    row1 = service.update(row1);
    assertEquals(1, isAllowedToUpdateCurrent.size());
    assertEquals(initialUuid, isAllowedToUpdateCurrent.get(0).getLinkToFullDownload());
    assertEquals(1, isAllowedToUpdate.size());
    assertEquals(newUuid, isAllowedToUpdate.get(0).getLinkToFullDownload());

    // delete single
    service.delete(row1);
    assertEquals(1, isAllowedToDelete.size());
    assertEquals(1, isAllowedToDelete.get(0).size());
    assertEquals("env1", isAllowedToDelete.get(0).get(0).getEnv());

    // delete single
    service.deleteByQuery(service.query().ge(TestDto1::getMajorVersion, 20));
    assertEquals(2, isAllowedToDelete.size());
    assertEquals(2, isAllowedToDelete.get(1).size());
    assertTrue(isAllowedToDelete.get(1).stream().anyMatch(x -> x.getEnv().equals("env2")));
    assertTrue(isAllowedToDelete.get(1).stream().anyMatch(x -> x.getEnv().equals("envX")));
  }
}
