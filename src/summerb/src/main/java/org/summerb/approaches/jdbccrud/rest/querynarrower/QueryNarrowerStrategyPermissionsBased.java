package org.summerb.approaches.jdbccrud.rest.querynarrower;

import java.util.List;
import java.util.stream.Collectors;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.microservices.users.api.PermissionService;
import org.summerb.microservices.users.api.dto.User;

/**
 * Default impl for narrower that finds objects user permitted to access.
 * 
 * WARNING: DO NOT USE it if you anticipate MANY objects of this type per user.
 * As no pagination is implemented here -- all ids retrieved at once
 */
public class QueryNarrowerStrategyPermissionsBased<TUser extends User> extends QueryNarrowerStrategyFieldBased {
	private PermissionService permissionService;
	private String optionalDomain;
	private String optionalRequiredPermission;
	private SecurityContextResolver<TUser> securityContextResolver;
	private Class<?> idClass;

	public QueryNarrowerStrategyPermissionsBased(PermissionService permissionService,
			SecurityContextResolver<TUser> securityContextResolver, Class<?> idClass, String optionalDomain,
			String optionalRequiredPermission) {
		super(HasId.FN_ID);
		this.permissionService = permissionService;
		this.securityContextResolver = securityContextResolver;
		this.idClass = idClass;
		this.optionalDomain = optionalDomain;
		this.optionalRequiredPermission = optionalRequiredPermission;
	}

	@Override
	protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
		List<String> idsStrs = permissionService.findSubjectsUserHasPermissionsFor(optionalDomain,
				securityContextResolver.getUserUuid(), optionalRequiredPermission);

		if (idClass.equals(String.class)) {
			if (idsStrs.size() == 0) {
				ret.eq(HasId.FN_ID, "NA");
			} else {
				ret.in(HasId.FN_ID, idsStrs.toArray(new String[0]));
			}
		} else {
			if (idsStrs.size() == 0) {
				// NOTE: Yes potentially there could be item with id = 0,
				// but still it's better to try that one instead of all
				// environments
				ret.eq(HasId.FN_ID, 0);
			} else {
				List<Long> idsLongs = idsStrs.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
				ret.in(HasId.FN_ID, idsLongs.toArray(new Long[0]));
			}
		}

		return ret;
	}
}