/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.summerb.dbupgrade.api.DbSchemaVersionResolver;

import com.google.common.base.Preconditions;

public class DbSchemaVersionResolverImpl implements DbSchemaVersionResolver {
	protected Logger log = LogManager.getLogger(getClass());

	private DataSource dataSource;
	private VersionTableDbDialect versionTableDbDialect;

	protected JdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	private boolean versioningTableEnsured;

	public DbSchemaVersionResolverImpl(DataSource dataSource,
			VersionTableDbDialect versionTableDbDialect) {
		Preconditions.checkArgument(dataSource != null, "dataSource required");
		Preconditions.checkArgument(versionTableDbDialect != null,
				"versionTableDbDialect required");

		this.dataSource = dataSource;
		this.versionTableDbDialect = versionTableDbDialect;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private void ensureVersionTrackingTablePresent() {
		if (versioningTableEnsured) {
			return;
		}
		try {
			jdbcTemplate.execute(versionTableDbDialect.getVersionTableExistanceCheck());
		} catch (Exception exc) {
			if (versionTableDbDialect.isTableMissingException(exc)) {
				createTable();
			} else {
				throw new RuntimeException("Failed to verify if database contains table for tracking db versions", exc);
			}
		}
		this.jdbcInsert = new SimpleJdbcInsert(dataSource)
				.withTableName(versionTableDbDialect.getTableName());
		versioningTableEnsured = true;
	}

	private void createTable() {
		try {
			jdbcTemplate.execute(versionTableDbDialect.getVersionTableCreationStatement());
			log.info("Create table for tracking DB version");
		} catch (Exception e2) {
			throw new RuntimeException("Failed to create table used for tracking DB version", e2);
		}
	}

	@Override
	public int getCurrentDbVersion() {
		try {
			ensureVersionTrackingTablePresent();
			Integer ret = jdbcTemplate.queryForObject(versionTableDbDialect.getCurrentDbVersionQuery(),
					Integer.class);
			return ret == null ? -1 : ret;
		} catch (Exception e) {
			throw new RuntimeException("Failed to query current db version", e);
		}
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
