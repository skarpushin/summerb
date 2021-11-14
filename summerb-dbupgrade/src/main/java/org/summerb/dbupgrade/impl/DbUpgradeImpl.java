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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.summerb.dbupgrade.DbUpgrade;
import org.summerb.dbupgrade.api.DbSchemaVersionResolver;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;

import com.google.common.base.Preconditions;

public class DbUpgradeImpl implements DbUpgrade {
	protected Logger log = LogManager.getLogger(getClass());

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
