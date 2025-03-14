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

import static org.junit.Assert.assertThrows;

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class EasyCrudServicePerRowAuthTest extends GenericCrudServiceTestTemplate {

  @Autowired
  @Qualifier("testDto1ServiceBasicAuth")
  private EasyCrudService<String, TestDto1> testDto1Service;

  @Autowired
  @Qualifier("testDto2ServiceBasicAuth")
  private EasyCrudService<Long, TestDto2> testDto2ServiceBasicAuth;

  @Autowired
  @Qualifier("testDto1ServiceBasicAuthEb")
  private EasyCrudService<String, TestDto1> testDto1ServiceEb;

  @Override
  public EasyCrudService<String, TestDto1> getTestDto1Service() {
    return testDto1Service;
  }

  @Override
  public EasyCrudService<Long, TestDto2> getTestDto2Service() {
    return testDto2ServiceBasicAuth;
  }

  @Override
  public EasyCrudService<String, TestDto1> getTestDto1ServiceEb() {
    return testDto1ServiceEb;
  }

  @Test
  public void testCreateDto2ExpectNae() {
    TestDto2 dto = new TestDto2();
    dto.setActive(true);
    dto.setEnv("throwNaeOnCreate");
    dto.setLinkToFullDownload("link-to-full-download1");
    dto.setMajorVersion(5);
    dto.setMinorVersion(6);

    assertThrows(NotAuthorizedException.class, () -> testDto2ServiceBasicAuth.create(dto));
  }
}
