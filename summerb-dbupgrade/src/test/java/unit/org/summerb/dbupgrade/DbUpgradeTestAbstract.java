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
package unit.org.summerb.dbupgrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.summerb.dbupgrade.DbUpgrade;
import org.summerb.dbupgrade.api.UpgradePackageBeanAbstract;
import org.summerb.dbupgrade.impl.VersionTableDbDialect;

public abstract class DbUpgradeTestAbstract {
  protected Logger log = LoggerFactory.getLogger(getClass());

  @Autowired private DbUpgrade dbUpgrade;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private VersionTableDbDialect versionTableDbDialect;

  @BeforeEach
  public void cleanUp() {
    safeDeleteTable("upg_pet");
    safeDeleteTable("db_version");
  }

  protected void safeDeleteTable(String tableName) {
    try {
      jdbcTemplate.update("DROP TABLE " + tableName);
      log.info("Removed table {}", tableName);
    } catch (Exception e) {
      if (versionTableDbDialect.isTableMissingException(e)) {
        log.info("Table was not there {}", tableName);
        return;
      }
      throw new RuntimeException("Failed to clean-up test db", e);
    }
  }

  @Test
  public void testDbUpgradeExpectSmokeTestPass() {
    assertEquals(-1, dbUpgrade.getCurrentDbVersion());
    assertEquals(3, dbUpgrade.getTargetDbVersion());
    dbUpgrade.upgrade();
    assertEquals(
        6, (int) jdbcTemplate.queryForObject("SELECT count(*) FROM upg_pet", Integer.class));
  }

  public static class CustomUpgradeStep extends UpgradePackageBeanAbstract {
    private JdbcTemplate jdbcTemplate;

    public CustomUpgradeStep(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void apply() {
      assertTrue(jdbcTemplate.update("INSERT INTO upg_pet (field1) VALUES ('5')") == 1);
    }
  }
}
