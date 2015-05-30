package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

public class NumberEqRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = 5063927291808835629L;

	private Long value;

	public NumberEqRestriction() {
	}

	public NumberEqRestriction(Long value) {
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public boolean isMeet(Long subjectValue) {
		if (value == null && subjectValue == null) {
			return !isNegative();
		}
		if (value == null || subjectValue == null) {
			return isNegative();
		}
		return isNegative() ? !value.equals(subjectValue) : value.equals(subjectValue);
	}
}
