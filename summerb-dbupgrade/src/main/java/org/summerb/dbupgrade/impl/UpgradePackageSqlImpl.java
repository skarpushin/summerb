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
package org.summerb.dbupgrade.impl;

import com.google.common.base.Preconditions;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageMeta;
import org.summerb.dbupgrade.api.UpgradeStatement;

public class UpgradePackageSqlImpl implements UpgradePackage {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected UpgradePackageMeta upgradePackageMeta;
  protected JdbcTemplate jdbcTemplate;
  protected SqlPackageParser sqlPackageParser;

  public UpgradePackageSqlImpl(
      UpgradePackageMeta upgradePackageMeta,
      SqlPackageParser sqlPackageParser,
      JdbcTemplate jdbcTemplate) {
    Preconditions.checkArgument(upgradePackageMeta != null, "upgradePackageMeta required");
    Preconditions.checkArgument(sqlPackageParser != null, "sqlPackageParser required");
    Preconditions.checkArgument(jdbcTemplate != null, "jdbcTemplate required");
    this.upgradePackageMeta = upgradePackageMeta;
    this.sqlPackageParser = sqlPackageParser;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int getId() {
    return upgradePackageMeta.getVersion();
  }

  @Override
  public String getName() {
    return upgradePackageMeta.getName();
  }

  @Override
  public void apply() throws Exception {
    try (InputStream is = upgradePackageMeta.getSource().get()) {
      sqlPackageParser.getUpgradeScriptsStream(is).forEach(this::execute);
    }
  }

  private void execute(UpgradeStatement upgradeStatement) {
    logStatement(upgradeStatement);
    jdbcTemplate.update(upgradeStatement.getStatement());
  }

  private void logStatement(UpgradeStatement upgradeStatement) {
    if (!log.isTraceEnabled()) {
      return;
    }
    if (upgradeStatement.getStatement().length() < 300) {
      log.trace("Executing statement: {}", upgradeStatement.getStatement());
    } else {
      log.trace(
          "Executing statement (trimmed): {}", upgradeStatement.getStatement().substring(0, 300));
    }
  }
}
