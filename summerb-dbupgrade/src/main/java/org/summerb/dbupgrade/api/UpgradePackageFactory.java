package org.summerb.dbupgrade.api;

/**
 * This factory will instantiate {@link UpgradePackage} based on
 * {@link UpgradePackageMeta}
 * 
 * @author sergeyk
 *
 */
public interface UpgradePackageFactory {

	boolean supports(UpgradePackageMeta upgradePackageMeta);

	UpgradePackage create(UpgradePackageMeta upgradePackageMeta);

}
