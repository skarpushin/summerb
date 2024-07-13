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


import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedMariaDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestEnumFieldType;
import integr.org.summerb.easycrud.testbeans.TestDto1Service;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedMariaDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
public class StressTest {
  @Autowired private TestDto1Service service;

  @Test
  void massiveAddition() {
    for (int i = 0; i < 100000; i++) {
      service.create(buildRow("env" + i, i));
    }
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
