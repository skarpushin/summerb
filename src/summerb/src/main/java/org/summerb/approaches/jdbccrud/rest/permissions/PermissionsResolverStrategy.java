package org.summerb.approaches.jdbccrud.rest.permissions;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;

/**
 * Impl of this interface will be used to resolve permissions and communicate it
 * to consumer along with the data
 * 
 * @author sergeyk
 */
public interface PermissionsResolverStrategy<TId, TDto extends HasId<TId>> {
	/**
	 * @param contextVariables
	 *            variables that defines current context that can be used for
	 *            table-wide permissions resolution
	 */
	void resolvePermissions(MultipleItemsResult<TId, TDto> ret, PathVariablesMap contextVariables);

	void resolvePermissions(SingleItemResult<TId, TDto> ret);
}