package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.HashMap;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;

import com.google.common.base.Preconditions;

public class PermissionsResolverStrategyRowAuthImpl<TId, TDto extends HasId<TId>>
		implements PermissionsResolverStrategy<TId, TDto> {

	private PermissionsResolverPerRow<TId, TDto> authStrategy;

	public PermissionsResolverStrategyRowAuthImpl(PermissionsResolverPerRow<TId, TDto> authStrategy) {
		Preconditions.checkArgument(authStrategy != null, "authStrategy must not be null");
		this.authStrategy = authStrategy;
	}

	@Override
	public void resolvePermissions(MultipleItemsResult<TId, TDto> ret, PathVariablesMap contextVariables) {
		ret.setRowPermissions(new HashMap<>());
		for (TDto row : ret.getRows()) {
			Map<String, Boolean> rowPerms = new HashMap<>();
			ret.getRowPermissions().put(row.getId(), rowPerms);
			resolve(row, contextVariables, rowPerms);
		}

		ret.setTablePermissions(new HashMap<>());
		resolve(null, contextVariables, ret.getTablePermissions());
	}

	@Override
	public void resolvePermissions(SingleItemResult<TId, TDto> ret) {
		ret.setPermissions(new HashMap<>());
		resolve(ret.getRow(), null, ret.getPermissions());
	}

	protected void resolve(TDto dto, PathVariablesMap contextVariables, Map<String, Boolean> ret) {
		ret.putAll(authStrategy.resolvePermissions(dto, contextVariables));
	}

}
