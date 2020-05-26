package org.summerb.dbupgrade.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.summerb.dbupgrade.api.UpgradePackageMeta;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;

import com.google.common.base.Preconditions;

public class UpgradePackageMetaResolverClasspathImpl implements UpgradePackageMetaResolver {
	protected ResourcePatternResolver resourcePatternResolver;
	protected String basePath;

	public UpgradePackageMetaResolverClasspathImpl(ResourcePatternResolver resourcePatternResolver, String basePath) {
		Preconditions.checkArgument(resourcePatternResolver != null, "resourcePatternResolver required");
		Preconditions.checkArgument(basePath != null, "basePath required");
		this.resourcePatternResolver = resourcePatternResolver;
		this.basePath = basePath;
	}

	@Override
	public int getMaximumPackageId() {
		try {
			Resource[] resources = resourcePatternResolver.getResources(basePath);
			return Arrays.stream(resources).map(this::fileToUpgradePackageMeta).mapToInt(x -> x.getVersion()).max()
					.orElse(-1);
		} catch (Exception e) {
			throw new RuntimeException("Failed to getMaximumPackageId", e);
		}
	}

	@Override
	public Stream<UpgradePackageMeta> getPackagesSince(int currentVersion) {
		try {
			Resource[] resources = resourcePatternResolver.getResources(basePath);
			return Arrays.stream(resources).map(this::fileToUpgradePackageMeta)
					.filter(x -> x.getVersion() > currentVersion).sorted((a, b) -> a.getVersion() - b.getVersion());
		} catch (Exception e) {
			throw new RuntimeException("Failed to getPackagesSince " + currentVersion, e);
		}
	}

	protected UpgradePackageMeta fileToUpgradePackageMeta(Resource r) {
		try {
			UpgradePackageMeta ret = new UpgradePackageMeta();
			String[] rawFileName = r.getFilename().split("_", 2);
			Preconditions.checkState(rawFileName.length == 2,
					"Upgrade script name \"%s\" does not comply to format: <version>_<name>.<extension>");
			ret.setVersion(Integer.parseInt(rawFileName[0]));
			ret.setName(FilenameUtils.getBaseName(rawFileName[1]));
			ret.setType(FilenameUtils.getExtension(rawFileName[1]));
			ret.setSource(() -> {
				try {
					return r.getInputStream();
				} catch (IOException e) {
					throw new RuntimeException("Failed to get input stream for resource", e);
				}
			});
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse UpgradePackageMeta from resource: " + r, t);
		}
	}

}
