package org.summerb.dbupgrade;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.UpgradePackageMetaResolverClasspathImpl;
import org.summerb.dbupgrade.impl.VersionTableDbDialect;
import org.summerb.dbupgrade.impl.postgress.SqlPackageParserPostgressImpl;
import org.summerb.dbupgrade.impl.postgress.VersionTableDbDialectPostgressImpl;

@Configuration
@PropertySource("test-dbconnection.properties")
public class TestPostgressConfig extends TestConfigBase {
	@Override
	protected UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception {
		return new UpgradePackageMetaResolverClasspathImpl(resourcePatternResolver, "classpath:/db_postgress/*");
	}

	@Bean
	@SuppressWarnings("deprecation")
	DataSource dataSource(@Value("${postgres.driverClassName}") String driver,
			@Value("${postgres.databaseurl}") String url, @Value("${postgres.username}") String username,
			@Value("${postgres.password}") String password) throws Exception {
		return new SimpleDriverDataSource((Driver) Class.forName(driver).newInstance(), url, username, password);
	}

	@Override
	@Bean
	protected SqlPackageParser sqlPackageParser() {
		return new SqlPackageParserPostgressImpl();
	}

	@Bean
	@Override
	protected VersionTableDbDialect versionTableDbDialect() {
		return new VersionTableDbDialectPostgressImpl();
	}

}
