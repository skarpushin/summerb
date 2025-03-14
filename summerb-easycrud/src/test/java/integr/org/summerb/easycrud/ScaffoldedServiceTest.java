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

import static org.junit.jupiter.api.Assertions.*;

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto2;
import integr.org.summerb.easycrud.testbeans.TestDto2Service;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class ScaffoldedServiceTest {
  @Autowired private TestDto2Service service;

  @Test
  void expect_scaffoldedServiceImplWorks() {
    createTestData();

    TestDto2 result = service.getForEnv("env2");
    assertNotNull(result);
    assertEquals("env2", result.getEnv());
  }

  @Test
  void expect_exceptionOnNoAffectedRowsDuringUpdate() {
    service.create(buildRow("env2", 20));
    TestDto2 result = service.getForEnv("env2");
    result.setModifiedAt(123); // set some invalid value
    assertThrows(OptimisticLockingFailureException.class, () -> service.update(result));
  }

  @Test
  void expect_exceptionOnNoAffectedRowsDuringDelete() throws InterruptedException {
    service.create(buildRow("env2", 20));
    TestDto2 modified = service.getForEnv("env2");
    TestDto2 unmodified = service.getForEnv("env2");
    Thread.sleep(100);
    modified.setLinkToFullDownload("asd");
    service.update(modified);

    assertThrows(OptimisticLockingFailureException.class, () -> service.delete(unmodified));
  }

  private void createTestData() {
    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));
  }

  private TestDto2 buildRow(String env, int majorVersion) {
    TestDto2 ret = new TestDto2();
    ret.setEnv(env);
    ret.setActive(true);
    ret.setMajorVersion(majorVersion);
    ret.setMinorVersion(1);
    ret.setLinkToFullDownload("link 1");
    return ret;
  }
}
