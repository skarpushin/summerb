/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.api.query;

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
	private static final long serialVersionUID = 8133148253107715041L;

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
	 * @param formData {@link PropertyAccessor} instance to access DTO fields
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
