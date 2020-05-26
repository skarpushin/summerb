package org.summerb.dbupgrade.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.summerb.dbupgrade.api.UpgradePackageMeta;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;

import com.google.common.base.Preconditions;

/**
 * NOTE: Works well with filesystem, but doesn't work when files are embedded in
 * the resources (classpath)
 * 
 * @author sergeyk
 *
 */
public class UpgradePackageMetaResolverFileImpl implements UpgradePackageMetaResolver {
	protected File basePath;

	public UpgradePackageMetaResolverFileImpl(File basePath) {
		Preconditions.checkArgument(basePath != null, "basePath required");
		this.basePath = basePath;
	}

	@Override
	public int getMaximumPackageId() {
		return Arrays.stream(basePath.listFiles()).map(this::fileToUpgradePackageMeta).mapToInt(x -> x.getVersion())
				.max().orElse(-1);
	}

	protected UpgradePackageMeta fileToUpgradePackageMeta(File file) {
		try {
			UpgradePackageMeta ret = new UpgradePackageMeta();
			String[] rawFileName = file.getName().split("_", 2);
			Preconditions.checkState(rawFileName.length == 2,
					"Upgrade script name \"%s\" does not comply to format: <version>_<name>.<extension>");
			ret.setVersion(Integer.parseInt(rawFileName[0]));
			ret.setName(FilenameUtils.getBaseName(rawFileName[1]));
			ret.setType(FilenameUtils.getExtension(rawFileName[1]));
			ret.setSource(() -> {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Failed to get input stream for file: " + file, e);
				}
			});
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse UpgradePackageMeta from file: " + file, t);
		}
	}

	@Override
	public Stream<UpgradePackageMeta> getPackagesSince(int currentVersion) {
		return Arrays.stream(basePath.listFiles()).map(this::fileToUpgradePackageMeta)
				.filter(x -> x.getVersion() > currentVersion).sorted((a, b) -> a.getVersion() - b.getVersion());
	}
}
