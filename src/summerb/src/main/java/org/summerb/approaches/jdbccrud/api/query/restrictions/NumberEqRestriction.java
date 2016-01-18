package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

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
		NumberEqRestriction other = (NumberEqRestriction) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
