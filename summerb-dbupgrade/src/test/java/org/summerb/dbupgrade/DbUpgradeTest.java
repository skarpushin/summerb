package org.summerb.dbupgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Driver;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.dbupgrade.DbUpgradeTest.TestConfig;
import org.summerb.dbupgrade.api.UpgradePackageBeanAbstract;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.UpgradePackageMetaResolverClasspathImpl;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class DbUpgradeTest {
	protected Logger log = Logger.getLogger(getClass());

	@Configuration
	@PropertySource("test-dbconnection.properties")
	public static class TestConfig extends DbUpgradeConfigAdapter {
		@Autowired
		private ResourcePatternResolver resourcePatternResolver;

		@Override
		protected UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception {
			return new UpgradePackageMetaResolverClasspathImpl(resourcePatternResolver, "classpath:/db1/*");
		}

		@Bean
		DataSource dataSource(@Value("${mysql.driverClassName}") String driver,
				@Value("${mysql.databaseurl}") String url, @Value("${mysql.username}") String username,
				@Value("${mysql.password}") String password) throws Exception {
			return new SimpleDriverDataSource((Driver) Class.forName(driver).newInstance(), url, username, password);
		}

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

	@Autowired
	private DbUpgrade dbUpgrade;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Before
	public void cleanUp() {
		safeDeleteTable("upg_pet");
		safeDeleteTable("db_version");
	}

	protected void safeDeleteTable(String tableName) {
		try {
			jdbcTemplate.update("DROP TABLE " + tableName);
			log.info("Removed table " + tableName);
		} catch (BadSqlGrammarException e) {
			MySQLSyntaxErrorException me = ExceptionUtils.findExceptionOfType(e, MySQLSyntaxErrorException.class);
			if (me != null && "42S02".equals(me.getSQLState())) {
				log.info("Table was not there " + tableName);
				return;
			}
			throw new RuntimeException("Failed to clean-up test db", e);
		}
	}

	@Test
	public void testDbUpgradeExpectSmokeTestPass() throws Exception {
		assertEquals(-1, dbUpgrade.getCurrentDbVersion());
		assertEquals(3, dbUpgrade.getTargetDbVersion());
		dbUpgrade.upgrade();
		assertTrue(6 == jdbcTemplate.queryForObject("SELECT count(*) FROM upg_pet", Integer.class));
	}

	public static class CustomUpgradeStep extends UpgradePackageBeanAbstract {
		private JdbcTemplate jdbcTemplate;

		public CustomUpgradeStep(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		@Override
		public void apply() throws Exception {
			assertTrue(jdbcTemplate.update("INSERT INTO upg_pet (`field1`) VALUES ('5')") == 1);
		}
	}
}
