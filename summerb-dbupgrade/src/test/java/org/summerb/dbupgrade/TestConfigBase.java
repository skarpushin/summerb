package org.summerb.dbupgrade;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.summerb.dbupgrade.DbUpgradeTestBase.CustomUpgradeStep;

abstract public class TestConfigBase extends DbUpgradeConfigAdapter {
	@Autowired
	protected ResourcePatternResolver resourcePatternResolver;

	@Bean
	PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	CustomUpgradeStep customUpgradeStep(JdbcTemplate jdbcTemplate) {
		return new CustomUpgradeStep(jdbcTemplate);
	}

	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}