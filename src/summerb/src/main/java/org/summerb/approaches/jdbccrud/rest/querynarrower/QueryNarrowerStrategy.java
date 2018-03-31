package org.summerb.approaches.jdbccrud.rest.querynarrower;

import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;

/**
 * Provide a sub-class that will alter queries before query will be issued to
 * service. I.e. useful to narrow queries only to items available to current
 * user OR which contained in current context identified by REST url path, i.e.
 * to select only devices which belong to a particular environment
 * <code>/rest/api/v1/env/{envId}/device</code>
 * 
 * @author sergeyk
 *
 */
public class QueryNarrowerStrategy {
	public Query narrow(Query optionalQuery, PathVariablesMap pathVariables) {
		return optionalQuery;
	}
}