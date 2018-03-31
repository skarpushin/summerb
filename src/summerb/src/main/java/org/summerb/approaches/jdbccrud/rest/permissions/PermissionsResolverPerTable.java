package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.EasyCrudTableAuthStrategy;

/**
 * Impl of this interface should be able to resolve current user permissions for
 * the whole table.
 * 
 * In most cases this interface will be implemented by the same class that
 * implements {@link EasyCrudTableAuthStrategy}.
 * 
 * @author sergeyk
 */
public interface PermissionsResolverPerTable {
	/**
	 * @return map of actions and allowances. For action constants see i.e.
	 *         {@link Permissions}
	 */
	Map<String, Boolean> resolvePermissions();
}
