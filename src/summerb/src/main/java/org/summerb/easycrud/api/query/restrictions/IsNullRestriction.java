package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

public class IsNullRestriction extends NegativableRestrictionBase implements Restriction<Object> {
	private static final long serialVersionUID = -1457132423443435523L;

	public IsNullRestriction() {
	}

	@Override
	public boolean isMeet(Object subjectValue) {
		return !isNegative() ? subjectValue == null : subjectValue != null;
	}
}
