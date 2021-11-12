package org.summerb.dbupgrade.impl;

public interface VersionTableDbDialect {

	String getVersionTableExistanceCheck();

	String getVersionTableCreationStatement();

	String getCurrentDbVersionQuery();

	boolean isTableMissingException(Exception exc);

	String getTableName();

}
