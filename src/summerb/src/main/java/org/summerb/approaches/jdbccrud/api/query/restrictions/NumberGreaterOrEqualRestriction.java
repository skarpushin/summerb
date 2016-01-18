package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

import com.google.common.base.Preconditions;

public class NumberGreaterOrEqualRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = 471006414190458384L;

	private long value;

	public NumberGreaterOrEqualRestriction() {
	}

	public NumberGreaterOrEqualRestriction(long value) {
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
		Preconditions.checkArgument(subjectValue != null);
		return isNegative() ? subjectValue < value : subjectValue >= value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
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
		NumberGreaterOrEqualRestriction other = (NumberGreaterOrEqualRestriction) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
