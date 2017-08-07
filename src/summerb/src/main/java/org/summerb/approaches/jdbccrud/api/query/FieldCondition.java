package org.summerb.approaches.jdbccrud.api.query;

import org.springframework.beans.PropertyAccessor;

/**
 * 
 * Subclass of {@link Restriction} particularly used to describe restriction
 * applied to a certain field refereed to as {@link #getFieldName()}.
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

	/**
	 * Method to verify restriction in-memory
	 * 
	 * @param formData
	 *            {@link PropertyAccessor} instance to access DTO fields
	 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((restriction == null) ? 0 : restriction.hashCode());
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
		FieldCondition other = (FieldCondition) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (restriction == null) {
			if (other.restriction != null)
				return false;
		} else if (!restriction.equals(other.restriction))
			return false;
		return true;
	}
}
