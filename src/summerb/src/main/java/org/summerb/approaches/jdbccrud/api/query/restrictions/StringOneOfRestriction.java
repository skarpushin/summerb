package org.summerb.approaches.jdbccrud.api.query.restrictions;

import java.util.Arrays;
import java.util.List;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

public class StringOneOfRestriction extends NegativableRestrictionBase implements Restriction<String> {
	private static final long serialVersionUID = 37882511862667146L;

	private List<String> values;

	public StringOneOfRestriction() {

	}

	public StringOneOfRestriction(List<String> values) {
		this.values = values;
	}

	public StringOneOfRestriction(String... values) {
		this.values = Arrays.asList(values);
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public boolean isMeet(String subjectValue) {
		if (values == null) {
			return !isNegative();
		}

		for (String value : values) {
			// NOTE: This is not 100% right since database might use
			// case-sensitive collation
			if (subjectValue.equalsIgnoreCase(value)) {
				return !isNegative();
			}
		}

		return isNegative();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		StringOneOfRestriction other = (StringOneOfRestriction) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
