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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.users.api.PermissionService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, UserServicesTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class PermissionsTest {

  @Autowired private PermissionService permissionService;

  @Test
  public void testGranPermission_expectPermissionWillBeFoundAfterCreation() {
    permissionService.grantPermission("domain", "user", "subject", "permission");

    List<String> results =
        permissionService.findUserPermissionsForSubject("domain", "user", "subject");
    assertTrue(results.contains("permission"));

    results = permissionService.findSubjectsUserHasPermissionsFor("domain", "user", "permission");
    assertTrue(results.contains("subject"));

    assertTrue(permissionService.hasPermission("domain", "user", "subject", "permission"));
  }

  @Test
  public void testRevokePermission_expectPermissionWillBeFoundAfterCreation() {
    permissionService.grantPermission("domain", "user", "subject", "permission");
    permissionService.revokePermission("domain", "user", "subject", "permission");

    List<String> results =
        permissionService.findUserPermissionsForSubject("domain", "user", "subject");
    assertEquals(0, results.size());

    results = permissionService.findSubjectsUserHasPermissionsFor("domain", "user", "permission");
    assertEquals(0, results.size());
  }

  @Test
  public void testRevokeUserPermissions_expectNoPermissionsAfterRevocation() {
    permissionService.grantPermission("domain", "user1", "subject1", "permission");
    permissionService.grantPermission("domain", "user1", "subject2", "permission");

    permissionService.revokeUserPermissions("domain", "user1");

    assertFalse(permissionService.hasPermission("domain", "user1", "subject1", "permission"));
    assertFalse(permissionService.hasPermission("domain", "user1", "subject2", "permission"));

    List<String> results =
        permissionService.findSubjectsUserHasPermissionsFor("domain", "user1", "permission");
    assertEquals(0, results.size());
  }

  @Test
  public void testRevokeAllPermissionsForSubject_expectNoPermissionsAfterClearence() {
    // preconditions
    permissionService.grantPermission("domain", "user1", "subject1", "permission");
    permissionService.grantPermission("domain", "user1", "subject2", "permission");

    // fixture
    permissionService.revokeAllPermissionsForSubject("domain", "subject1");

    // test
    assertFalse(permissionService.hasPermission("domain", "user1", "subject1", "permission"));
    assertTrue(permissionService.hasPermission("domain", "user1", "subject2", "permission"));

    List<String> results =
        permissionService.findSubjectsUserHasPermissionsFor("domain", "user1", "permission");
    assertEquals(1, results.size());
    assertTrue(results.contains("subject2"));
  }

  @Test
  public void testGetSubjectsUserHasPermissionsFor_expectNoPermissionsAfterClearence() {
    // preconditions
    permissionService.grantPermission(null, "user1", "subject1", "permission1");
    permissionService.grantPermission(null, "user1", "subject2", "permission1");
    permissionService.grantPermission(null, "user1", "subject2", "permission2");
    permissionService.grantPermission(null, "user1", "subject3", "permission2");

    // fixture
    List<String> results =
        permissionService.findSubjectsUserHasPermissionsFor(null, "user1", "permission2");
    assertEquals(2, results.size());
    assertTrue(results.contains("subject2"));
    assertTrue(results.contains("subject3"));

    results = permissionService.findSubjectsUserHasPermissionsFor(null, "user1", null);
    assertEquals(4, results.size());
  }

  @Test
  public void testGetUsersAndTheirPermissionsForSubject_expectAllAppropriateDataWillBeReturned() {
    permissionService.grantPermission(null, "user1", "subject1", "permission1");
    permissionService.grantPermission(null, "user2", "subject1", "permission1");
    permissionService.grantPermission(null, "user2", "subject1", "permission2");

    Map<String, List<String>> results =
        permissionService.findUsersAndTheirPermissionsForSubject(null, "subject1");

    assertEquals(2, results.size());
    assertEquals(1, results.get("user1").size());
    assertEquals(2, results.get("user2").size());
  }
}
