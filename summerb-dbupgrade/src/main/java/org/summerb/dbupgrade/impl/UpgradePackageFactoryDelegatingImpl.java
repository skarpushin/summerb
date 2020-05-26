package org.summerb.dbupgrade.impl;

import java.util.function.Supplier;

import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageFactoryResolver;
import org.summerb.dbupgrade.api.UpgradePackageMeta;

public class UpgradePackageFactoryDelegatingImpl implements UpgradePackageFactory {
	protected UpgradePackageFactoryResolver upgradePackageFactory;

	public UpgradePackageFactoryDelegatingImpl(UpgradePackageFactoryResolver upgradePackageFactory) {
		this.upgradePackageFactory = upgradePackageFactory;
	}

	@Override
	public boolean supports(UpgradePackageMeta upgradePackageMeta) {
		return upgradePackageFactory.getFactories().stream().anyMatch(x -> x.supports(upgradePackageMeta));
	}

	@Override
	public UpgradePackage create(UpgradePackageMeta upgradePackageMeta) {
		Supplier<? extends IllegalArgumentException> nfeException = () -> new IllegalArgumentException(
				"No UpgradePackageFactory found for " + upgradePackageMeta);
		return upgradePackageFactory.getFactories().stream().filter(x -> x.supports(upgradePackageMeta))
				.map(x -> x.create(upgradePackageMeta)).findFirst().orElseThrow(nfeException);
	}
}
