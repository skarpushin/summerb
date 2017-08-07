package org.summerb.approaches.jdbccrud.api.query;

import java.util.Arrays;

import org.springframework.beans.PropertyAccessor;

/**
 * By default {@link Query} adds all restrictions in conjunction. In order to
 * use disjunction add instance of this {@link DisjunctionCondition} to root
 * {@link Query}
 * 
 * @author sergey.karpushin
 *
 */
public class DisjunctionCondition implements Restriction<PropertyAccessor> {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(queries);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisjunctionCondition other = (DisjunctionCondition) obj;
		if (!Arrays.equals(queries, other.queries))
			return false;
		return true;
	}

}
