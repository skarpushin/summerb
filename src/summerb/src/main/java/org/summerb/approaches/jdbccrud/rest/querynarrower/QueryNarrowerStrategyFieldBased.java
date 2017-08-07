package org.summerb.approaches.jdbccrud.rest.querynarrower;

import org.springframework.beans.PropertyAccessor;
import org.summerb.approaches.jdbccrud.api.query.DisjunctionCondition;
import org.summerb.approaches.jdbccrud.api.query.FieldCondition;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.api.query.Restriction;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;

public abstract class QueryNarrowerStrategyFieldBased extends QueryNarrowerStrategy {
	private String fieldName;

	public QueryNarrowerStrategyFieldBased(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public Query narrow(Query optionalQuery, PathVariablesMap pathVariables) {
		Query ret;
		if (optionalQuery == null) {
			ret = Query.n();
		} else {
			if (hasRestrictionOnField(optionalQuery, fieldName)) {
				return optionalQuery;
			}
			ret = optionalQuery;
		}

		ret = doNarrow(ret, pathVariables);

		return ret;
	}

	protected boolean hasRestrictionOnField(Query query, String fieldName) {
		for (Restriction<PropertyAccessor> restriction : query.getRestrictions()) {
			if (restriction instanceof FieldCondition) {
				if (fieldName.equals(((FieldCondition) restriction).getFieldName())) {
					return true;
				}
			} else if (restriction instanceof DisjunctionCondition) {
				for (Query subQuery : ((DisjunctionCondition) restriction).getQueries()) {
					if (hasRestrictionOnField(subQuery, fieldName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected abstract Query doNarrow(Query ret, PathVariablesMap allRequestParams);
}