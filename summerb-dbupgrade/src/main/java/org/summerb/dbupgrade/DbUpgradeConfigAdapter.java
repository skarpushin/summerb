package org.summerb.dbupgrade;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.summerb.dbupgrade.api.DbSchemaVersionResolver;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageFactoryResolver;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.DbSchemaVersionResolverMySqlImpl;
import org.summerb.dbupgrade.impl.DbUpgradeImpl;
import org.summerb.dbupgrade.impl.SqlPackageParserImpl;
import org.summerb.dbupgrade.impl.UpgradePackageFactoryBeanImpl;
import org.summerb.dbupgrade.impl.UpgradePackageFactoryDelegatingImpl;
import org.summerb.dbupgrade.impl.UpgradePackageFactoryResolverSpringAutodiscoverImpl;
import org.summerb.dbupgrade.impl.UpgradePackageFactorySqlImpl;

public abstract class DbUpgradeConfigAdapter {
	@Bean
	protected DbUpgrade dbUpgrade(UpgradePackageMetaResolver upgradePackageMetaResolver,
			DbSchemaVersionResolver dbSchemaVersionResolver, UpgradePackageFactory upgradePackageFactory) {
		return new DbUpgradeImpl(upgradePackageMetaResolver, dbSchemaVersionResolver, upgradePackageFactory);
	}

	@Bean
	protected abstract UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception;

	@Bean
	protected DbSchemaVersionResolver dbSchemaVersionResolver(DataSource dataSource) {
		return new DbSchemaVersionResolverMySqlImpl(dataSource);
	}

	@Bean
	protected UpgradePackageFactoryResolver upgradePackageFactoryResolver() {
		return new UpgradePackageFactoryResolverSpringAutodiscoverImpl();
	}

	@Bean
	@Primary
	protected UpgradePackageFactory upgradePackageFactory(UpgradePackageFactoryResolver upgradePackageFactoryResolver) {
		return new UpgradePackageFactoryDelegatingImpl(upgradePackageFactoryResolver);
	}

	@Bean
	protected UpgradePackageFactory upgradePackageFactoryBean(ApplicationContext applicationContext) {
		return new UpgradePackageFactoryBeanImpl(applicationContext);
	}

	@Bean
	protected UpgradePackageFactory upgradePackageFactorySql(DataSource dataSource, SqlPackageParser sqlPackageParser) {
		return new UpgradePackageFactorySqlImpl(new JdbcTemplate(dataSource), sqlPackageParser);
	}

	@Bean
	protected SqlPackageParser sqlPackageParser() {
		return new SqlPackageParserImpl();
	}
}
