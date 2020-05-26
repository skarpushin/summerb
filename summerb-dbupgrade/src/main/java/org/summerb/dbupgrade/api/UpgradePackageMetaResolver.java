package org.summerb.dbupgrade.api;

import java.util.stream.Stream;

/**
 * Discovers meta information of available upgrade packages
 * 
 * @author sergeyk
 *
 */
public interface UpgradePackageMetaResolver {

	int getMaximumPackageId();

	Stream<UpgradePackageMeta> getPackagesSince(int currentVersion);

}
