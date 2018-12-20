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
		Object val = allRequestParams.get(commonParamName);
		if (val instanceof String) {
			ret.eq(commonParamName, (String) val);
		} else if (val instanceof Long) {
			ret.eq(commonParamName, (Long) val);
		} else {
			throw new IllegalStateException("Current impl doesn't support args of type: " + val.getClass());
		}
		return ret;
	}
}