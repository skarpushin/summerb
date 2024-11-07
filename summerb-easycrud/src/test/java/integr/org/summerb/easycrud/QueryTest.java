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
package integr.org.summerb.easycrud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
import integr.org.summerb.easycrud.dtos.TestEnumFieldType;
import integr.org.summerb.easycrud.testbeans.CustomMapperToTestDto2;
import integr.org.summerb.easycrud.testbeans.TestDto1Service;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
@ProfileValueSourceConfiguration()
@Transactional
public class QueryTest {
  @Autowired private TestDto1Service service;

  @Test
  void expect_getEnvMaxWithScalarParam_works() {
    createTestData();

    String result = service.getEnvMaxWithScalarParam(20);
    assertEquals("env2", result);
  }

  @Test
  void expect_getEnvCountNoParams_works() {
    createTestData();

    int result = service.getEnvCountNoParams();
    assertEquals(3, result);
  }

  @Test
  void expect_getEnvsWithArray_works() {
    createTestData();

    List<String> result = service.getEnvsWithArray(new int[] {10, 30});
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0));
    assertEquals("env3", result.get(1));
  }

  @Test
  void expect_getEnvsWithSet_works() {
    createTestData();

    List<String> result = service.getEnvsWithSet(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0));
    assertEquals("env3", result.get(1));
  }

  @Test
  void expect_getDtosWithSet_works() {
    createTestData();

    List<TestDto1> result = service.getDtosWithSet(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0).getEnv());
    assertEquals("env3", result.get(1).getEnv());
  }

  @Test
  void expect_getDtosWithSetAndCustomMapper_works() {
    createTestData();

    List<TestDto2> result = service.getDtosWithSetAndCustomMapper(Set.of(10, 30));
    assertEquals(2, result.size());
    assertEquals("env1", result.get(0).getEnv());
    assertEquals(CustomMapperToTestDto2.CUSTOM_MAPPER, result.get(0).getLinkToFullDownload());

    assertEquals("env3", result.get(1).getEnv());
    assertEquals(CustomMapperToTestDto2.CUSTOM_MAPPER, result.get(1).getLinkToFullDownload());
  }

  @Test
  void expect_defaultSqlOverrideForEnumWorks_works() {
    createTestData();

    List<TestDto1> result = service.getDtosWithSet(Set.of(10, 30));
    assertEquals(TestEnumFieldType.ACTIVE, result.get(0).getLinkToPatchToNextVersion());
  }

  private void createTestData() {
    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));
  }

  private TestDto1 buildRow(String env, int majorVersion) {
    TestDto1 ret = new TestDto1();
    ret.setEnv(env);
    ret.setActive(true);
    ret.setMajorVersion(majorVersion);
    ret.setMinorVersion(1);
    ret.setLinkToFullDownload("link 1");
    ret.setLinkToPatchToNextVersion(
        majorVersion % 2 == 0 ? TestEnumFieldType.ACTIVE : TestEnumFieldType.INACTIVE);
    return ret;
  }
}
