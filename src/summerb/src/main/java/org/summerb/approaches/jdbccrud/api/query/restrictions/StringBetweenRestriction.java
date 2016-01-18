package org.summerb.approaches.jdbccrud.api.query.restrictions;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

import com.google.common.base.Preconditions;

public class StringBetweenRestriction extends NegativableRestrictionBase implements Restriction<String> {
	private static final long serialVersionUID = -1804000515828431204L;

	private String lowerBound;
	private String upperBound;

	public StringBetweenRestriction() {

	}

	public StringBetweenRestriction(String lowerBound, String upperBound) {
		Preconditions.checkArgument(lowerBound != null);
		Preconditions.checkArgument(upperBound != null);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public boolean isMeet(String subjectValue) {
		if (subjectValue == null) {
			return isNegative();
		}

		boolean lessThanLower = subjectValue.compareToIgnoreCase(lowerBound) < 0;
		boolean lessThanUpper = upperBound.compareToIgnoreCase(subjectValue) > 0;
		boolean greaterOrEqual = lowerBound.compareToIgnoreCase(subjectValue) <= 0;
		boolean lessOrEqual = subjectValue.compareToIgnoreCase(upperBound) <= 0;
		return isNegative() ? lessThanLower || lessThanUpper : greaterOrEqual && lessOrEqual;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((lowerBound == null) ? 0 : lowerBound.hashCode());
		result = prime * result + ((upperBound == null) ? 0 : upperBound.hashCode());
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
		StringBetweenRestriction other = (StringBetweenRestriction) obj;
		if (lowerBound == null) {
			if (other.lowerBound != null)
				return false;
		} else if (!lowerBound.equals(other.lowerBound))
			return false;
		if (upperBound == null) {
			if (other.upperBound != null)
				return false;
		} else if (!upperBound.equals(other.upperBound))
			return false;
		return true;
	}

}
