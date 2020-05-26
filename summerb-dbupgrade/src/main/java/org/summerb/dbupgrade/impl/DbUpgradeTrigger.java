package org.summerb.dbupgrade.impl;

import org.summerb.dbupgrade.DbUpgrade;

import com.google.common.base.Preconditions;

public class DbUpgradeTrigger {
	public DbUpgradeTrigger(DbUpgrade dbUpgrade) {
		Preconditions.checkArgument(dbUpgrade != null, "dbUpgrade required");
		try {
			dbUpgrade.upgrade();
		} catch (Throwable e) {
			throw new RuntimeException("Upgrade trigger failed", e);
		}
	}

}
