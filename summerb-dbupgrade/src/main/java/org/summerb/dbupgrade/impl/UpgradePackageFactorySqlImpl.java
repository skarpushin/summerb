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
package org.summerb.dbupgrade.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageMeta;

import com.google.common.base.Preconditions;

public class UpgradePackageFactorySqlImpl implements UpgradePackageFactory {
	protected static final String EXTENSION = "sql";

	protected JdbcTemplate jdbcTemplate;
	protected SqlPackageParser sqlPackageParser;

	public UpgradePackageFactorySqlImpl(JdbcTemplate jdbcTemplate, SqlPackageParser sqlPackageParser) {
		Preconditions.checkArgument(jdbcTemplate != null, "jdbcTemplate required");
		Preconditions.checkArgument(sqlPackageParser != null, "sqlPackageParser required");
		this.jdbcTemplate = jdbcTemplate;
		this.sqlPackageParser = sqlPackageParser;
	}

	@Override
	public UpgradePackage create(UpgradePackageMeta upgradePackageMeta) {
		return new UpgradePackageSqlImpl(upgradePackageMeta, sqlPackageParser, jdbcTemplate);
	}

	@Override
	public boolean supports(UpgradePackageMeta upgradePackageMeta) {
		return EXTENSION.equalsIgnoreCase(upgradePackageMeta.getType());
	}
}
