package org.summerb.dbupgrade.api;

/**
 * Simple DTO that contains information needed to execute 1 statement within
 * {@link UpgradePackage}
 * 
 * @author sergeyk
 *
 */
public class UpgradeStatement {

	private String statement;

	public UpgradeStatement(String statement) {
		this.statement = statement;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

}
