/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.validation;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

public class AggregatedObjectValidationErrors extends ValidationError implements HasValidationErrors {
	private static final long serialVersionUID = 1661526473131036543L;

	private List<ValidationError> validationErrors;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public AggregatedObjectValidationErrors() {
	}

	/**
	 * 
	 * @param fieldToken
	 *            name of the field which references custom object (not just simple
	 *            scalar variable) which was validated and contains errors. In case
	 *            if this DTO is used to represent object in collection, this field
	 *            might be used to identify collection item instance (it might be
	 *            item idx, guid or any other thing suitable for end-application)
	 * @param childErrors
	 *            list of child errors for this object
	 */
	public AggregatedObjectValidationErrors(String fieldToken, List<ValidationError> childErrors) {
		super("validation.aggregatedObjectError", fieldToken);
		Preconditions.checkArgument(childErrors != null, "childErrors must not be null");
		this.validationErrors = childErrors;
	}

	public void setValidationErrors(List<ValidationError> childErrors) {
		this.validationErrors = childErrors;
	}

	@Override
	public List<? extends ValidationError> getValidationErrors() {
		return validationErrors;
	}

	@Override
	public boolean getHasErrors() {
		return validationErrors.size() > 0;
	}

	public <T> T findErrorOfType(Class<T> clazz) {
		return ValidationErrorsUtils.findErrorOfType(clazz, validationErrors);
	}

	public <T> T findErrorOfTypeForField(Class<T> clazz, String fieldToken) {
		return ValidationErrorsUtils.findErrorOfTypeForField(clazz, fieldToken, validationErrors);
	}

	@Override
	public String toString() {
		if (CollectionUtils.isEmpty(validationErrors)) {
			return super.toString() + " (empty)";
		}

		StringBuilder ret = new StringBuilder();

		for (ValidationError ve : validationErrors) {
			if (ret.length() > 0) {
				ret.append("; ");
			}

			ret.append(ve.toString());
		}

		return getClass().getSimpleName() + " (idx = '" + getFieldToken() + "'): " + ret.toString();
	}
}
