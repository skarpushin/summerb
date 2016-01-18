package org.summerb.approaches.jdbccrud.api.query.restrictions;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

public class StringLengthBetweenRestriction implements Restriction<String>, Serializable {
	private static final long serialVersionUID = 8702933533968549326L;

	private long lowerBound;
	private long upperBound;

	public StringLengthBetweenRestriction() {

	}

	public StringLengthBetweenRestriction(long lowerBound, long upperBound) {
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
	public boolean isMeet(String subjectValue) {
		int len = subjectValue == null ? 0 : subjectValue.length();
		return lowerBound <= len && len <= upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = prime * result + (int) (upperBound ^ (upperBound >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringLengthBetweenRestriction other = (StringLengthBetweenRestriction) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (upperBound != other.upperBound)
			return false;
		return true;
	}
}
