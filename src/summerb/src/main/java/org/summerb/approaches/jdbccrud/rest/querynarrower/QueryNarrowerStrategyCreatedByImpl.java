package org.summerb.approaches.jdbccrud.rest.querynarrower;

import org.summerb.approaches.jdbccrud.api.dto.HasAuthor;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.microservices.users.api.dto.User;

/**
 * This narrower simply trims query to only those items which were created by
 * current user
 * 
 * WARNING: It's assummed dto implemented interface {@link HasAuthor}
 */
public class QueryNarrowerStrategyCreatedByImpl<TUser extends User> extends QueryNarrowerStrategyFieldBased {
	private SecurityContextResolver<TUser> securityContextResolver;

	public QueryNarrowerStrategyCreatedByImpl(SecurityContextResolver<TUser> securityContextResolver) {
		super(HasAuthor.FN_CREATED_BY);
		this.securityContextResolver = securityContextResolver;
	}

	@Override
	protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
		ret.eq(HasAuthor.FN_CREATED_BY, securityContextResolver.getUserUuid());
		return ret;
	}
}