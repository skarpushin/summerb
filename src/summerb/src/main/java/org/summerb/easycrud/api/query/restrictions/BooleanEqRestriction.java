package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

import com.google.common.base.Preconditions;

public class BooleanEqRestriction extends NegativableRestrictionBase implements Restriction<Boolean> {
	private static final long serialVersionUID = 1809030822888453382L;

	private boolean value;

	public BooleanEqRestriction() {
	}

	public BooleanEqRestriction(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public boolean isMeet(Boolean subjectValue) {
		Preconditions.checkArgument(subjectValue != null);
		return isNegative() ? value != subjectValue : value == subjectValue;
	}

}
