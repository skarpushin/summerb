package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

public class StringContainsRestriction extends NegativableRestrictionBase implements Restriction<String> {
	private static final long serialVersionUID = -5385241631791772520L;

	private String value;

	public StringContainsRestriction() {
	}

	public StringContainsRestriction(String value) {
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
		boolean result = subjectValue.toLowerCase().contains(value.toLowerCase());
		return isNegative() ? !result : result;
	}
}
