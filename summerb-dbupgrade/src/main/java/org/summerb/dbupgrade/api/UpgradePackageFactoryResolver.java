package org.summerb.dbupgrade.api;

import java.util.List;

/**
 * This strategy knows how to discover instances of
 * {@link UpgradePackageFactory}
 * 
 * @author sergeyk
 *
 */
public interface UpgradePackageFactoryResolver {

	List<UpgradePackageFactory> getFactories();

}
