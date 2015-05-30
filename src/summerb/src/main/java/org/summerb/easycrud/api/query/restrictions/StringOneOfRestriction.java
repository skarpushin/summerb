package org.summerb.easycrud.api.query.restrictions;

import java.util.Arrays;
import java.util.List;

import org.summerb.easycrud.api.query.Restriction;

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
}
