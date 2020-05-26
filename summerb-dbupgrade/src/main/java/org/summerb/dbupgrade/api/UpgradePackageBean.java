package org.summerb.dbupgrade.api;

public interface UpgradePackageBean extends UpgradePackage {
	/**
	 * Set information resolved from {@link UpgradePackageMeta}
	 */
	void setMeta(int id, String name);
}
