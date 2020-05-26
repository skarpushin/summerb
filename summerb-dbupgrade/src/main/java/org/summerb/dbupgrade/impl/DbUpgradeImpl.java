package org.summerb.dbupgrade.impl;

import org.apache.log4j.Logger;
import org.summerb.dbupgrade.DbUpgrade;
import org.summerb.dbupgrade.api.DbSchemaVersionResolver;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;

import com.google.common.base.Preconditions;

public class DbUpgradeImpl implements DbUpgrade {
	protected Logger log = Logger.getLogger(getClass());

	protected UpgradePackageMetaResolver upgradePackageMetaResolver;
	protected DbSchemaVersionResolver dbSchemaVersionResolver;
	protected UpgradePackageFactory upgradePackageFactory;

	public DbUpgradeImpl(UpgradePackageMetaResolver upgradePackageMetaResolver,
			DbSchemaVersionResolver dbSchemaVersionResolver, UpgradePackageFactory upgradePackageFactory) {
		Preconditions.checkArgument(upgradePackageMetaResolver != null, "upgradePackageMetaResolver required");
		Preconditions.checkArgument(dbSchemaVersionResolver != null, "dbSchemaVersionResolver required");
		Preconditions.checkArgument(upgradePackageFactory != null, "upgradePackageFactory required");
		this.upgradePackageMetaResolver = upgradePackageMetaResolver;
		this.dbSchemaVersionResolver = dbSchemaVersionResolver;
		this.upgradePackageFactory = upgradePackageFactory;
	}

	@Override
	public int getCurrentDbVersion() {
		return dbSchemaVersionResolver.getCurrentDbVersion();
	}

	@Override
	public int getTargetDbVersion() {
		return upgradePackageMetaResolver.getMaximumPackageId();
	}

	@Override
	public void upgrade() throws Exception {
		try {
			int currentDbVersion = getCurrentDbVersion();
			int targetDbVersion = getTargetDbVersion();
			if (targetDbVersion == currentDbVersion) {
				log.info("Database schema version is up-to-date: " + targetDbVersion);
				return;
			}
			log.info("Database schema version will be upgraded from " + currentDbVersion + " to "
					+ getTargetDbVersion());
			upgradePackageMetaResolver.getPackagesSince(currentDbVersion).map(upgradePackageFactory::create)
					.forEach(this::applyPackage);
		} catch (Throwable t) {
			throw new RuntimeException("Migration failed", t);
		}
	}

	protected void applyPackage(UpgradePackage upgradePackage) {
		try {
			log.info("Applying db upgrade package #" + upgradePackage.getId() + ": " + upgradePackage.getName());
			upgradePackage.apply();
			dbSchemaVersionResolver.increaseVersionTo(upgradePackage.getId());
		} catch (Throwable t) {
			throw new RuntimeException("Applying db upgrade package #" + upgradePackage.getId() + ": "
					+ upgradePackage.getName() + " have failed", t);
		}
	}
}
