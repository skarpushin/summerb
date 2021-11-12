package org.summerb.dbupgrade;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.UpgradePackageMetaResolverClasspathImpl;

@Configuration
@PropertySource("test-dbconnection.properties")
public class TestMySqlConfig extends TestConfigBase {
	@Override
	protected UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception {
		return new UpgradePackageMetaResolverClasspathImpl(resourcePatternResolver, "classpath:/db_mysql/*");
	}

	@Bean
	@SuppressWarnings("deprecation")
	DataSource dataSource(@Value("${mysql.driverClassName}") String driver, @Value("${mysql.databaseurl}") String url,
			@Value("${mysql.username}") String username, @Value("${mysql.password}") String password) throws Exception {
		return new SimpleDriverDataSource((Driver) Class.forName(driver).newInstance(), url, username, password);
	}

}
