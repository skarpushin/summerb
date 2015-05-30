package org.summerb.easycrud.api.query;

import org.springframework.beans.PropertyAccessor;

/**
 * 
 * @author sergey.karpushin
 *
 */
@SuppressWarnings("rawtypes")
public class FieldCondition implements Restriction<PropertyAccessor> {
	private Restriction restriction;
	private String fieldName;

	public FieldCondition() {

	}

	public FieldCondition(String fieldName, Restriction restriction) {
		this.fieldName = fieldName;
		this.restriction = restriction;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isMeet(PropertyAccessor formData) {
		return restriction.isMeet(formData.getPropertyValue(fieldName));
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
