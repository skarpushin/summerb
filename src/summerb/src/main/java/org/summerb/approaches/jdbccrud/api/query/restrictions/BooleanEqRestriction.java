package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (value ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BooleanEqRestriction other = (BooleanEqRestriction) obj;
		if (value != other.value)
			return false;
		return true;
	}

}
