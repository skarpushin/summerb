package org.summerb.easycrud.api.query;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.PropertyAccessor;
import org.summerb.easycrud.api.query.restrictions.BooleanEqRestriction;
import org.summerb.easycrud.api.query.restrictions.IsNullRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberEqRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberGreaterOrEqualRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringEqRestriction;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringOneOfRestriction;

/**
 * Entry point for configuring filter query to the form data
 * 
 * @author sergey.karpushin
 * 
 */
public class Query {
	private List<Restriction<PropertyAccessor>> list;

	public Query() {
		list = new LinkedList<Restriction<PropertyAccessor>>();
	}

	/**
	 * Shortcut for creating new instance of this class
	 */
	public static Query n() {
		return new Query();
	}

	public boolean isMeet(PropertyAccessor subjectValue) {
		for (Restriction<PropertyAccessor> r : list) {
			if (!r.isMeet(subjectValue)) {
				return false;
			}
		}
		return true;
	}

	public List<Restriction<PropertyAccessor>> getRestrictions() {
		return list;
	}

	public Query or(Query a, Query b) {
		list.add(new DisjunctionCondition(a, b));
		return this;
	}

	public Query isNull(String fieldName) {
		list.add(new FieldCondition(fieldName, new IsNullRestriction()));
		return this;
	}

	public Query isNotNull(String fieldName) {
		IsNullRestriction r = new IsNullRestriction();
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query eq(String fieldName, String value) {
		list.add(new FieldCondition(fieldName, new StringEqRestriction(value)));
		return this;
	}

	public Query ne(String fieldName, String value) {
		StringEqRestriction r = new StringEqRestriction(value);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query lengthBetween(String fieldName, int minLength, int maxLength) {
		list.add(new FieldCondition(fieldName, new StringLengthBetweenRestriction(minLength, maxLength)));
		return this;
	}

	public Query in(String fieldName, String... values) {
		list.add(new FieldCondition(fieldName, new StringOneOfRestriction(values)));
		return this;
	}

	public Query notIn(String fieldName, String... values) {
		StringOneOfRestriction notIn = new StringOneOfRestriction(values);
		notIn.setNegative(true);
		list.add(new FieldCondition(fieldName, notIn));
		return this;
	}

	public Query contains(String fieldName, String value) {
		list.add(new FieldCondition(fieldName, new StringContainsRestriction(value)));
		return this;
	}

	public Query notContains(String fieldName, String value) {
		StringContainsRestriction r = new StringContainsRestriction(value);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query isTrue(String fieldName) {
		list.add(new FieldCondition(fieldName, new BooleanEqRestriction(true)));
		return this;
	}

	public Query isFalse(String fieldName) {
		list.add(new FieldCondition(fieldName, new BooleanEqRestriction(false)));
		return this;
	}

	public Query ne(String fieldName, boolean value) {
		BooleanEqRestriction r = new BooleanEqRestriction(value);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query eq(String fieldName, long value) {
		list.add(new FieldCondition(fieldName, new NumberEqRestriction(value)));
		return this;
	}

	public Query ne(String fieldName, long value) {
		NumberEqRestriction r = new NumberEqRestriction(value);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query in(String fieldName, Long... values) {
		list.add(new FieldCondition(fieldName, new NumberOneOfRestriction(values)));
		return this;
	}

	public Query notIn(String fieldName, Long... values) {
		NumberOneOfRestriction r = new NumberOneOfRestriction(values);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query between(String fieldName, long lowerBound, long upperBound) {
		list.add(new FieldCondition(fieldName, new NumberBetweenRestriction(lowerBound, upperBound)));
		return this;
	}

	public Query notBetween(String fieldName, long lowerBound, long upperBound) {
		NumberBetweenRestriction r = new NumberBetweenRestriction(lowerBound, upperBound);
		r.setNegative(true);
		list.add(new FieldCondition(fieldName, r));
		return this;
	}

	public Query ge(String fieldName, long value) {
		list.add(new FieldCondition(fieldName, new NumberGreaterOrEqualRestriction(value)));
		return this;
	}

}
