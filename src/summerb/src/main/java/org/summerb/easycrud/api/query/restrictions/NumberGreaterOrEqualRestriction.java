package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

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
}
