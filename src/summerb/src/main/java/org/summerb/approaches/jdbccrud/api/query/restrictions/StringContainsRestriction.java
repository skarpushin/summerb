package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringContainsRestriction other = (StringContainsRestriction) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
