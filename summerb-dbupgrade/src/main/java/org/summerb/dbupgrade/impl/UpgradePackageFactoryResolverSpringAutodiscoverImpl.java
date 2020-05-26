package org.summerb.dbupgrade.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageFactoryResolver;

import com.google.common.base.Preconditions;

/**
 * This impl will just find all beans of type {@link UpgradePackageFactory}
 * except {@link UpgradePackageFactoryDelegatingImpl}
 * 
 * @author sergeyk
 *
 */
public class UpgradePackageFactoryResolverSpringAutodiscoverImpl implements UpgradePackageFactoryResolver {
	@Autowired
	protected ApplicationContext ctx;
	protected List<UpgradePackageFactory> factories;

	@Override
	public List<UpgradePackageFactory> getFactories() {
		if (factories == null) {
			String[] names = ctx.getBeanNamesForType(UpgradePackageFactory.class);
			Preconditions.checkState(names.length > 0, "No beans of type UpgradePackageFactory have been found");
			List<UpgradePackageFactory> found = Arrays.stream(names)
					.filter(x -> !ctx.isTypeMatch(x, UpgradePackageFactoryDelegatingImpl.class))
					.map(x -> ctx.getBean(x, UpgradePackageFactory.class)).collect(Collectors.toList());
			Preconditions.checkState(found.size() > 0,
					"No beans of type UpgradePackageFactory have been found (UpgradePackageFactoryDelegatingImpl bean doesn't count)");
			this.factories = found;
		}
		return factories;
	}
}
