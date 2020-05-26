package org.summerb.dbupgrade;

/**
 * Primary entry point for this little tool. Allows you to understand database
 * versions and trigger upgrade
 * 
 * @author sergeyk
 *
 */
public interface DbUpgrade {

	int getCurrentDbVersion();

	int getTargetDbVersion();

	void upgrade() throws Exception;

}
