package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;

public interface PermissionsResolverPerRow<TId, TDto extends HasId<TId>> {
	Map<String, Boolean> resolvePermissions(TDto optionalDto, PathVariablesMap contextVariables);
}
