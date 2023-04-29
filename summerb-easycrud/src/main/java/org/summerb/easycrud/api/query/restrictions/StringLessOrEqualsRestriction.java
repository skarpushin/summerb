package org.summerb.easycrud.api.query.restrictions;

public class StringLessOrEqualsRestriction extends NegatableRestrictionBase<String> {
	private static final long serialVersionUID = -4260677481548959434L;

	private String value;

	public StringLessOrEqualsRestriction() {
	}

	public StringLessOrEqualsRestriction(String value) {
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
			return true;
		}
		if (value != null && subjectValue == null) {
			return isNegative();
		}
		if (value == null && subjectValue != null) {
			return !isNegative();
		}

		// NOTE: This is not 100% right since database might use case-sensitive
		// collation
		int result = value.toLowerCase().compareTo(subjectValue.toLowerCase());
		return !isNegative() ? result <= 0 : result > 0;
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
		StringLessOrEqualsRestriction other = (StringLessOrEqualsRestriction) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
