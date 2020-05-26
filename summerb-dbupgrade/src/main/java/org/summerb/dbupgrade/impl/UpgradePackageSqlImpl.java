package org.summerb.dbupgrade.impl;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageMeta;
import org.summerb.dbupgrade.api.UpgradeStatement;

import com.google.common.base.Preconditions;

public class UpgradePackageSqlImpl implements UpgradePackage {
	protected Logger log = Logger.getLogger(getClass());

	protected UpgradePackageMeta upgradePackageMeta;
	protected JdbcTemplate jdbcTemplate;
	protected SqlPackageParser sqlPackageParser;

	public UpgradePackageSqlImpl(UpgradePackageMeta upgradePackageMeta, SqlPackageParser sqlPackageParser,
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
			// List<String> statements = sqlPackageParser.getUpgradeScriptsStream(is).map(x
			// -> x.getStatement())
			// .collect(Collectors.toList());
			// jdbcTemplate.batchUpdate(statements.toArray(new String[0]));
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
			log.trace("Executing statement: " + upgradeStatement.getStatement());
		} else {
			log.trace("Executing statement (trimmed): " + upgradeStatement.getStatement().substring(0, 300));
		}
	}
}
