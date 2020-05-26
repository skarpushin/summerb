package org.summerb.dbupgrade.api;

import org.springframework.context.annotation.Lazy;

/**
 * Special type of Upgrade package which represents a java bean. SO instead of
 * making changes described as sql, some "manual" java code changes will be
 * applied.
 * 
 * It is recommended to mark such beans as {@link Lazy} so that they are
 * instantiated only when needed
 */
public abstract class UpgradePackageBeanAbstract implements UpgradePackageBean {
	private int id;
	private String name;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setMeta(int id, String name) {
		this.id = id;
		this.name = name;
	}
}
