package org.summerb.easycrud.api.query.restrictions;

import java.io.Serializable;

import org.summerb.easycrud.api.query.Restriction;

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
}
