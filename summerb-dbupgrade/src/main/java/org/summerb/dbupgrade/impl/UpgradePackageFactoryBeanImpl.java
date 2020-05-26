package org.summerb.dbupgrade.impl;

import org.springframework.context.ApplicationContext;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageBean;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageMeta;

import com.google.common.base.Preconditions;

public class UpgradePackageFactoryBeanImpl implements UpgradePackageFactory {
	protected static final String EXTENSION = "bean";

	protected ApplicationContext applicationContext;

	public UpgradePackageFactoryBeanImpl(ApplicationContext applicationContext) {
		Preconditions.checkArgument(applicationContext != null, "applicationContext required");
		this.applicationContext = applicationContext;
	}

	@Override
	public UpgradePackage create(UpgradePackageMeta upgradePackageMeta) {
		Preconditions.checkArgument(supports(upgradePackageMeta), "Not supported: %s", upgradePackageMeta);
		UpgradePackageBean ret = applicationContext.getBean(upgradePackageMeta.getName(), UpgradePackageBean.class);
		ret.setMeta(upgradePackageMeta.getVersion(), upgradePackageMeta.getName());
		return ret;
	}

	@Override
	public boolean supports(UpgradePackageMeta upgradePackageMeta) {
		return EXTENSION.equalsIgnoreCase(upgradePackageMeta.getType())
				&& applicationContext.isTypeMatch(upgradePackageMeta.getName(), UpgradePackageBean.class);
	}

}
