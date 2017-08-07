package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.Map;

public interface PermissionsResolverPerTable {
	Map<String, Boolean> resolvePermissions();
}
