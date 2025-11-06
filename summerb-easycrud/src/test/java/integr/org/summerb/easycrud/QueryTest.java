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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.dtos.UserStatus;
import integr.org.summerb.easycrud.testbeans.UserRowCustomMapper;
import integr.org.summerb.easycrud.testbeans.UserRowService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.exceptions.EasyCrudUnexpectedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
@ProfileValueSourceConfiguration()
@Transactional
public class QueryTest {
  @Autowired private UserRowService service;

  @Test
  void expect_getEnvMaxWithScalarParam_works() {
    createTestData();

    String result = service.getNameMaxWithScalarParam(20);
    assertEquals("env2", result);
  }

  @Test
  void expect_defaultMethodWorks() {
    createTestData();

    UserRow result = service.getUsingDefault(20);
    assertEquals("env1", result.getName());
  }

  @Test
  void expect_getEnvCountNoParams_works() {
    createTestData();

    int result = service.getNameCountNoParams();
    assertEquals(3, result);
  }

  @Test
  void expect_getEnvsWithArray_works() {
    createTestData();

    List<String> result = service.getNamesWithArray(new int[] {10, 30});
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0));
    assertEquals("env3", result.get(1));
  }

  @Test
  void expect_getEnvsWithArray_worksWithSetAsReturnType() {
    createTestData();

    Set<String> result = service.getNamesWithArrayAsSet(new int[] {10, 30});
    assertEquals(2, result.size());
    assertEquals(Set.of("env1", "env3"), result);
  }

  @Test
  void expect_getEnvsWithSet_works() {
    createTestData();

    List<String> result = service.getNamesWithSet(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0));
    assertEquals("env3", result.get(1));
  }

  @Test
  void expect_getDtosWithSet_works() {
    createTestData();

    List<UserRow> result = service.getDtosWithSet(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0).getName());
    assertEquals("env3", result.get(1).getName());
  }

  @Test
  void expect_getDtosWithSetAndCustomMapper_works() {
    createTestData();

    List<UserRow> result = service.getDtosWithSetAndCustomMapper(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0).getName());
    assertEquals(UserRowCustomMapper.CUSTOM_MAPPER, result.get(0).getAbout());

    assertEquals("env3", result.get(1).getName());
    assertEquals(UserRowCustomMapper.CUSTOM_MAPPER, result.get(1).getAbout());
  }

  @Test
  void expect_defaultSqlOverrideForEnumWorks_works() {
    createTestData();

    List<UserRow> result = service.getDtosWithSet(Set.of(10, 30));
    assertEquals(UserStatus.ACTIVE, result.get(0).getStatus());
  }

  @Test
  void test_updateReturnVoid() {
    UserRow row = service.create(buildRow("env1", 30));
    String someUuid = UUID.randomUUID().toString();
    service.updateReturnVoid(row.getId(), someUuid);
    UserRow retrieved = service.getById(row.getId());
    assertEquals(someUuid, retrieved.getAbout());
  }

  @Test
  void test_updateReturnInt() {
    UserRow row = service.create(buildRow("env1", 30));
    String someUuid = UUID.randomUUID().toString();
    int affectedRows = service.updateReturnInt(row.getId(), someUuid);
    assertEquals(1, affectedRows);
    UserRow retrieved = service.getById(row.getId());
    assertEquals(someUuid, retrieved.getAbout());
  }

  @Test
  void test_updateReturnIntBoxed() {
    UserRow row = service.create(buildRow("env1", 30));
    String someUuid = UUID.randomUUID().toString();
    Integer affectedRows = service.updateReturnIntBoxed(row.getId(), someUuid);
    assertEquals(1, affectedRows);
    UserRow retrieved = service.getById(row.getId());
    assertEquals(someUuid, retrieved.getAbout());
  }

  @Test
  void test_updateReturnInt_expect0() {
    int affectedRows = service.updateReturnInt(UUID.randomUUID().toString(), "asd");
    assertEquals(0, affectedRows);
  }

  @Test
  void test_updateReturnInt_expectExceptionOnWrongReturnType() {
    assertThrows(
        EasyCrudUnexpectedException.class, () -> service.updateReturnWrongReturnType("asd", "asd"));
  }

  private void createTestData() {
    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));
  }

  public static UserRow buildRow(String name, int karma) {
    UserRow ret = new UserRow();
    ret.setName(name);
    ret.setActive(true);
    ret.setKarma(karma);
    ret.setAbout("link 1");
    ret.setStatus(karma % 2 == 0 ? UserStatus.ACTIVE : UserStatus.INACTIVE);
    return ret;
  }
}
