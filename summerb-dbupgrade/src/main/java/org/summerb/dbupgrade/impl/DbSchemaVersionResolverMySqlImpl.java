package org.summerb.dbupgrade.impl;

import java.sql.SQLSyntaxErrorException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.StringUtils;
import org.summerb.dbupgrade.api.DbSchemaVersionResolver;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.google.common.base.Preconditions;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class DbSchemaVersionResolverMySqlImpl implements DbSchemaVersionResolver {
	protected Logger log = Logger.getLogger(getClass());

	protected JdbcTemplate jdbcTemplate;
	protected final String tableName;
	protected SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	private boolean versioningTableEnsured;

	public DbSchemaVersionResolverMySqlImpl(DataSource dataSource) {
		this(dataSource, "db_version");
	}

	public DbSchemaVersionResolverMySqlImpl(DataSource dataSource, String tableName) {
		this.dataSource = dataSource;
		Preconditions.checkArgument(dataSource != null, "dataSource required");
		Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName required");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.tableName = tableName;
	}

	private void ensureVersionTrackingTablePresent() {
		if (versioningTableEnsured) {
			return;
		}
		try {
			jdbcTemplate.execute(String.format("SELECT count(*) FROM %s", tableName));
		} catch (Exception exc) {
			SQLSyntaxErrorException grammarException = ExceptionUtils.findExceptionOfType(exc,
					SQLSyntaxErrorException.class);
			if (grammarException != null && "42S02".equals(grammarException.getSQLState())) {
				createTable();
			} else {
				throw new RuntimeException("Failed to verify if database contains table for tracking db versions", exc);
			}
		}
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);
		versioningTableEnsured = true;
	}

	private void createTable() {
		try {
			jdbcTemplate.execute(String.format("CREATE TABLE `%s` ( `version` INT NOT NULL )", tableName));
			log.info("Create table for tracking DB version");
		} catch (Exception e2) {
			throw new RuntimeException("Failed to create table used for tracking DB version", e2);
		}
	}

	@Override
	public int getCurrentDbVersion() {
		try {
			ensureVersionTrackingTablePresent();
			Integer ret = jdbcTemplate.queryForObject(String.format("SELECT MAX(version) FROM `%s`", tableName),
					Integer.class);
			return ret == null ? -1 : ret;
		} catch (Exception e) {
			if (isMissingTableException(e)) {
				return -1;
			}
			throw new RuntimeException("Failed to query current db version", e);
		}
	}

	protected boolean isMissingTableException(Exception e) {
		MySQLSyntaxErrorException me = ExceptionUtils.findExceptionOfType(e, MySQLSyntaxErrorException.class);
		return me != null && "42S02".equals(me.getSQLState());
	}

	@Override
	public void increaseVersionTo(int version) {
		try {
			ensureVersionTrackingTablePresent();

			Preconditions.checkArgument(getCurrentDbVersion() < version,
					"New version %s must be higher than current %s", version, getCurrentDbVersion());

			BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(new DbVersion(version));
			jdbcInsert.execute(params);
		} catch (Exception e) {
			throw new RuntimeException("Failed to increase DB version to " + version, e);
		}
	}

	public static class DbVersion {
		private int version;

		public DbVersion() {
		}

		public DbVersion(int version) {
			this.version = version;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}
	}
}
