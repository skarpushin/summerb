/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package unit.org.summerb.dbupgrade;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import unit.org.summerb.dbupgrade.config.TestPostgressConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestPostgressConfig.class})
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.POSTGRES, refresh = RefreshMode.AFTER_CLASS)
public class DbUpgradePostgressTest extends DbUpgradeTestAbstract {
  // NOTE: All impl is in parnt. The only difference is config whether we use
  // MySql or Postgress specifics

}
