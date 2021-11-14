/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.summerb.i18n.HasMessageCode;
import org.summerb.utils.exceptions.HasErrorDescriptionObject;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class FieldValidationException extends Exception
		implements HasMessageCode, HasErrorDescriptionObject<ValidationErrors> {
	private static final long serialVersionUID = -310812271204903287L;

	protected List<ValidationError> errors = new ArrayList<ValidationError>();

	public FieldValidationException() {
	}

	public FieldValidationException(ValidationError validationError) {
		errors.add(validationError);
	}

	public FieldValidationException(List<ValidationError> validationErrors) {
		errors.addAll(validationErrors);
	}

	public List<ValidationError> getErrors() {
		return errors;
	}

	@Override
	public String getMessageCode() {
		return "validation.error";
	}

	public boolean hasErrorOfType(Class<?> clazz) {
		return ValidationErrorsUtils.hasErrorOfType(clazz, errors);
	}

	public <T> T findErrorOfType(Class<T> clazz) {
		return ValidationErrorsUtils.findErrorOfType(clazz, errors);
	}

	public List<ValidationError> findErrorsForField(String fieldToken) {
		return ValidationErrorsUtils.findErrorsForField(fieldToken, errors);
	}

	public <T> T findErrorOfTypeForField(Class<T> clazz, String fieldToken) {
		return ValidationErrorsUtils.findErrorOfTypeForField(clazz, fieldToken, errors);
	}

	@Override
	public String toString() {
		if (CollectionUtils.isEmpty(errors)) {
			return super.toString() + " (empty)";
		}

		StringBuilder ret = new StringBuilder();

		for (ValidationError ve : errors) {
			if (ret.length() > 0) {
				ret.append("; ");
			}

			ret.append(ve.toString());
		}

		return "FieldValidationException: " + ret.toString();
	}

	@Override
	public ValidationErrors getErrorDescriptionObject() {
		return new ValidationErrors(errors);
	}
}
