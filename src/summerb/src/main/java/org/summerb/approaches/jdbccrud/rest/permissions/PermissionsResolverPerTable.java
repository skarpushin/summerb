package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.Map;

public interface PermissionsResolverPerTable {
	/**
	 * @return map of actions and allowances. For action contants see i.e.
	 *         {@link Permissions}
	 */
	Map<String, Boolean> resolvePermissions();
}
