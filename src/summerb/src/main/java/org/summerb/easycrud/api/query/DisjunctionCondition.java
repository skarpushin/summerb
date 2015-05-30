package org.summerb.easycrud.api.query;

import org.springframework.beans.PropertyAccessor;

/**
 * 
 * @author sergey.karpushin
 *
 */public class DisjunctionCondition implements Restriction<PropertyAccessor> {
	private Query[] queries;

	public DisjunctionCondition() {
	}

	public DisjunctionCondition(Query... queries) {
		this.queries = queries;
	}

	@Override
	public boolean isMeet(PropertyAccessor subjectValue) {
		for (int i = 0; i < queries.length; i++) {
			if (queries[i].isMeet(subjectValue)) {
				return true;
			}
		}
		return false;
	}

	public Query[] getQueries() {
		return queries;
	}

	public void setQueries(Query[] queries) {
		this.queries = queries;
	}

}
