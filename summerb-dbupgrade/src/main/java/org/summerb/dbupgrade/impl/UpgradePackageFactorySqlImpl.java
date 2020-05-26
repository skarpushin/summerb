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
