package org.summerb.dbupgrade.impl.mysql;

import java.sql.SQLSyntaxErrorException;

import org.springframework.util.StringUtils;
import org.summerb.dbupgrade.impl.VersionTableDbDialect;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.google.common.base.Preconditions;

public class VersionTableDbDialectMySqlImpl implements VersionTableDbDialect {
	public static final String TABLE_DEFAULT_NAME = "db_version";

	protected final String tableName;

	public VersionTableDbDialectMySqlImpl() {
		this(TABLE_DEFAULT_NAME);
	}

	public VersionTableDbDialectMySqlImpl(String tableName) {
		Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName required");
		this.tableName = tableName;
	}

	@Override
	public String getVersionTableExistanceCheck() {
		return String.format("SELECT count(*) FROM %s", getTableName());
	}

	@Override
	public String getVersionTableCreationStatement() {
		return String.format("CREATE TABLE %s ( version INT NOT NULL )", getTableName());
	}

	@Override
	public String getCurrentDbVersionQuery() {
		return String.format("SELECT MAX(version) FROM %s", getTableName());
	}

	@Override
	public boolean isTableMissingException(Exception exc) {
		SQLSyntaxErrorException grammarException = ExceptionUtils.findExceptionOfType(exc,
				SQLSyntaxErrorException.class);
		return grammarException != null && "42S02".equals(grammarException.getSQLState());
	}

	@Override
	public String getTableName() {
		return tableName;
	}
}
