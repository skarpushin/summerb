package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.Restriction;

public class NumberBetweenRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = -7489366611902539609L;

	private long lowerBound;
	private long upperBound;

	public NumberBetweenRestriction() {

	}

	public NumberBetweenRestriction(long lowerBound, long upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(long lowerBound) {
		this.lowerBound = lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(long upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public boolean isMeet(Long subjectValue) {
		if (subjectValue == null) {
			return isNegative();
		}

		return isNegative() ? subjectValue < lowerBound || upperBound > subjectValue : lowerBound <= subjectValue
				&& subjectValue <= upperBound;
	}
}
