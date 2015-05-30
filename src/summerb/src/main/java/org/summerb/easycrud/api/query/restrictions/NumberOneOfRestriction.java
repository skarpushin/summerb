package org.summerb.easycrud.api.query.restrictions;

import java.util.Arrays;
import java.util.List;

import org.summerb.easycrud.api.query.Restriction;

public class NumberOneOfRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = 4538148232283195229L;

	private List<Long> values;

	public NumberOneOfRestriction() {

	}

	public NumberOneOfRestriction(List<Long> values) {
		this.values = values;
	}

	public NumberOneOfRestriction(Long... values) {
		this.values = Arrays.asList(values);
	}

	public List<Long> getValues() {
		return values;
	}

	public void setValues(List<Long> values) {
		this.values = values;
	}

	@Override
	public boolean isMeet(Long subjectValue) {
		if (values == null) {
			return !isNegative();
		}
		return isNegative() ? !values.contains(subjectValue) : values.contains(subjectValue);
	}
}
