package org.summerb.dbupgrade.api;

/**
 * This strategy knows how to determine and set current DB version
 * 
 * @author sergeyk
 *
 */
public interface DbSchemaVersionResolver {

	int getCurrentDbVersion();

	void increaseVersionTo(int version);

}
