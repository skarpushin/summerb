package org.summerb.dbupgrade.api;

import org.springframework.transaction.annotation.Transactional;

/**
 * Represents UpgradePackage which can be applied. Behavior depends on impl.
 * 
 * @author sergeyk
 *
 */
public interface UpgradePackage {

	int getId();

	String getName();

	@Transactional(noRollbackFor = Throwable.class)
	void apply() throws Exception;

}
