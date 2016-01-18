package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

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

		return isNegative() ? subjectValue < lowerBound || upperBound > subjectValue
				: lowerBound <= subjectValue && subjectValue <= upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = prime * result + (int) (upperBound ^ (upperBound >>> 32));
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
		NumberBetweenRestriction other = (NumberBetweenRestriction) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (upperBound != other.upperBound)
			return false;
		return true;
	}
}
