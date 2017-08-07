package org.summerb.approaches.jdbccrud.rest.permissions;

import java.util.HashMap;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;

import com.google.common.base.Preconditions;

public class PermissionsResolverStrategyTableAuthImpl<TId, TDto extends HasId<TId>>
		implements PermissionsResolverStrategy<TId, TDto> {

	private PermissionsResolverPerTable tableAuthStrategy;

	public PermissionsResolverStrategyTableAuthImpl(PermissionsResolverPerTable tableAuthStrategy) {
		Preconditions.checkArgument(tableAuthStrategy != null, "authStrategy must not be null");
		this.tableAuthStrategy = tableAuthStrategy;
	}

	@Override
	public void resolvePermissions(MultipleItemsResult<TId, TDto> ret, PathVariablesMap contextVariables) {
		ret.setTablePermissions(new HashMap<>());
		resolveEasyCrudTableAuthStrategyPermissions(ret.getTablePermissions());
	}

	@Override
	public void resolvePermissions(SingleItemResult<TId, TDto> ret) {
		ret.setPermissions(new HashMap<>());
		resolveEasyCrudTableAuthStrategyPermissions(ret.getPermissions());
	}

	protected void resolveEasyCrudTableAuthStrategyPermissions(Map<String, Boolean> ret) {
		ret.putAll(tableAuthStrategy.resolvePermissions());
	}

}
