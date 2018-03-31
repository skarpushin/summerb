package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.summerb.approaches.jdbccrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.HasCommonPathVariable;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.HasCommonPathVariables;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;

/**
 * Impl of this interface should be able to resolve current user permissions for
 * particular DTO.
 * 
 * In most cases this interface will be implemented by the same class that
 * implements {@link EasyCrudPerRowAuthStrategy}.
 * 
 * @author sergeyk
 */
public interface PermissionsResolverPerRow<TId, TDto extends HasId<TId>> {

	/**
	 * @param optionalDto
	 *            could be null. In this case it means we need to provide
	 *            table-wide permissions
	 * 
	 * @param contextVariables
	 *            context variables which were resolved from
	 *            {@link RequestMapping} based on Controller-wide
	 *            {@link HasCommonPathVariable} and
	 *            {@link HasCommonPathVariables} annotations.
	 * 
	 * @return pairs: permission - availability. For standard action constants
	 *         see {@link Permissions}
	 */
	Map<String, Boolean> resolvePermissions(TDto optionalDto, PathVariablesMap contextVariables);
}
