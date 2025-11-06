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

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.dtos.UserStatus;
import integr.org.summerb.easycrud.testbeans.UserRowService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
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
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class PerformanceTest {
  @Autowired private UserRowService service;

  @Test
  void massiveAdditions() {
    for (int i = 0; i < 100000; i++) {
      service.create(buildRow("env" + i, i));
    }
  }

  @Test
  void massiveUpdates() {
    UserRow row = service.create(buildRow("env", 1));
    for (int i = 2; i < 100000; i++) {
      row.setKarma(i);
      row.setName("env" + i);
      row = service.update(row);
    }

    // NOTE: Updates are a bit slower than additions because under the hood they use vanilla
    // MqpSqlParameterSource for restrictions and those are not optimized by EasyCrud. But compared
    // to previous issues with performance with Spring's SimpleJdbcInsert, it doesn't look critical
    // to optimize update performance. SO for now I'm leaving it as is.
  }

  private UserRow buildRow(String env, int majorVersion) {
    UserRow ret = new UserRow();
    ret.setName(env);
    ret.setActive(true);
    ret.setKarma(majorVersion);
    ret.setAbout("link 1");
    ret.setStatus(majorVersion % 2 == 0 ? UserStatus.ACTIVE : UserStatus.INACTIVE);
    return ret;
  }
}
