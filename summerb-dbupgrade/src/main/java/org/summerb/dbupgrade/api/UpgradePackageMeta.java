package org.summerb.dbupgrade.api;

import java.io.InputStream;
import java.util.function.Supplier;

public class UpgradePackageMeta {
	private int version;
	private String name;
	private String type;
	private Supplier<InputStream> source;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String extension) {
		this.type = extension;
	}

	@Override
	public String toString() {
		return "UpgradePackageMeta [version=" + version + ", name=" + name + ", type=" + type + "]";
	}

	public Supplier<InputStream> getSource() {
		return source;
	}

	public void setSource(Supplier<InputStream> source) {
		this.source = source;
	}
}
