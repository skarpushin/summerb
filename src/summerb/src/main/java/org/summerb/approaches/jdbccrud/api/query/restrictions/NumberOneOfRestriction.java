package org.summerb.approaches.jdbccrud.api.query.restrictions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.summerb.approaches.jdbccrud.api.query.Restriction;

public class NumberOneOfRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = 4538148232283195229L;

	private Set<Long> values;

	public NumberOneOfRestriction() {

	}

	public NumberOneOfRestriction(Set<Long> values) {
		this.values = values;
	}

	public NumberOneOfRestriction(Long... values) {
		this.values = new HashSet<>(Arrays.asList(values));
	}

	public Set<Long> getValues() {
		return values;
	}

	public void setValues(Set<Long> values) {
		this.values = values;
	}

	@Override
	public boolean isMeet(Long subjectValue) {
		if (values == null) {
			return !isNegative();
		}
		return isNegative() ? !values.contains(subjectValue) : values.contains(subjectValue);
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
		NumberOneOfRestriction other = (NumberOneOfRestriction) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
