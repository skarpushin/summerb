package org.summerb.approaches.jdbccrud.rest.querynarrower;

import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;

public class QueryNarrowerByCommonPathVariable extends QueryNarrowerStrategyFieldBased {
	private String commonParamName;

	public QueryNarrowerByCommonPathVariable(String commonParamName) {
		super(commonParamName);
		this.commonParamName = commonParamName;
	}

	@Override
	protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
		long envId = (long) allRequestParams.get(commonParamName);
		ret.eq(commonParamName, envId);
		return ret;
	}
}