package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

public class StringEqRestriction extends NegativableRestrictionBase implements Restriction<String> {
	private static final long serialVersionUID = -5623781369783055479L;

	private String value;

	public StringEqRestriction() {
	}

	public StringEqRestriction(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isMeet(String subjectValue) {
		if (value == null && subjectValue == null) {
			return !isNegative();
		}
		if (value == null || subjectValue == null) {
			return isNegative();
		}

		// NOTE: This is not 100% right since database might use case-sensitive
		// collation
		boolean result = value.equalsIgnoreCase(subjectValue);
		return isNegative() ? !result : result;
	}
}
