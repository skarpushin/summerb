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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.StringUtils;
import org.summerb.validation.errors.DataTooLongValidationError;
import org.summerb.validation.errors.FieldRequiredValidationError;
import org.summerb.validation.errors.InvalidEmailValidationError;
import org.summerb.validation.errors.MustBeEqualsValidationError;
import org.summerb.validation.errors.MustBeGreaterOrEqualValidationError;
import org.summerb.validation.errors.MustBeGreaterValidationError;
import org.summerb.validation.errors.MustBeLessOrEqualValidationError;
import org.summerb.validation.errors.NotANumberValidationError;
import org.summerb.validation.errors.NumberOutOfRangeValidationError;
import org.summerb.validation.errors.StringTooShortValidationError;
import org.summerb.validation.errors.TooShortStringValidationError;

/**
 * This class contains a list of accumulated validation errors.
 * 
 * <p>
 * 
 * You can either add them manually by using {@link #add(ValidationError)}, or
 * you can use provided methods like
 * {@link #validateEmailFormat(String, String)} or
 * {@link #validateNotEmpty(String, String)} or others.
 * 
 * <p>
 * 
 * And then you supposed to call {@link #throwIfHasErrors()} which will throw
 * {@link FieldValidationException} with all those validation errors.
 * 
 * @author sergey.karpushin
 *
 */
public class ValidationContext {
	// TBD: Fix patter. It will fail if we'll pass here regular UUID
	public static final String regexpEmail = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";

	private List<ValidationError> errors = new LinkedList<ValidationError>();

	public ValidationContext() {

	}

	public boolean getHasErrors() {
		return getErrors().size() > 0;
	}

	public List<ValidationError> getErrors() {
		return errors;
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

	public void add(ValidationError validationError) {
		errors.add(validationError);
	}

	public boolean validateEmailFormat(String email, String fieldToken) {
		if (isValidEmail(email)) {
			return true;
		}

		add(new InvalidEmailValidationError(fieldToken));
		return false;
	}

	public boolean validateIsBit(String stringRepresentation, String fieldToken) {
		try {
			int parsed = Integer.parseInt(stringRepresentation);
			if (parsed != 0 && parsed != 1) {
				add(new NumberOutOfRangeValidationError(parsed, 0, 1, fieldToken));
				return false;
			}
			return true;
		} catch (NumberFormatException t) {
			add(new NotANumberValidationError(fieldToken));
			return false;
		}
	}

	public boolean validateIsByte(String stringRepresentation, String fieldToken) {
		try {
			int parsed = Integer.parseInt(stringRepresentation);
			if (parsed < Byte.MIN_VALUE || parsed > Byte.MAX_VALUE) {
				add(new NumberOutOfRangeValidationError(parsed, Byte.MIN_VALUE, Byte.MAX_VALUE, fieldToken));
				return false;
			}
			return true;
		} catch (NumberFormatException t) {
			add(new NotANumberValidationError(fieldToken));
			return false;
		}
	}

	public boolean validateIsLong(String stringRepresentation, String fieldToken) {
		try {
			Long.parseLong(stringRepresentation);
			return true;
		} catch (NumberFormatException t) {
			add(new NotANumberValidationError(fieldToken));
			return false;
		}
	}

	public boolean validateNotEmpty(String str, String fieldToken) {
		if (StringUtils.hasText(str)) {
			return true;
		}

		add(new FieldRequiredValidationError(fieldToken));
		return false;
	}

	public boolean validateNotEmpty(Long lvalue, String fieldToken) {
		if (lvalue != null && lvalue != 0) {
			return true;
		}

		add(new FieldRequiredValidationError(fieldToken));
		return false;
	}

	public boolean validateLengthGreaterOrEqual(String str, long minLength, String fieldToken) {
		long len = str == null ? 0 : str.length();
		if (len >= minLength) {
			return true;
		}

		add(new TooShortStringValidationError(minLength, fieldToken));
		return false;
	}

	public boolean validateDataLengthLessOrEqual(String str, int maxLength, String fieldToken) {
		int len = str == null ? 0 : str.length();
		if (len <= maxLength) {
			return true;
		}

		add(new DataTooLongValidationError(len, maxLength, fieldToken));
		return false;
	}

	public boolean validateNotEmpty(Collection<?> collection, String fieldToken) {
		if (collection != null && collection.size() > 0) {
			return true;
		}

		add(new FieldRequiredValidationError(fieldToken));
		return false;
	}

	public boolean validateNotNull(Object obj, String fieldToken) {
		if (obj != null) {
			return true;
		}

		add(new FieldRequiredValidationError(fieldToken));
		return false;
	}

	public static boolean isValidEmail(String userEmail) {
		return userEmail != null && userEmail.contains("@") && userEmail.contains(".") && userEmail.length() > 5
				&& userEmail.matches(regexpEmail);
	}

	public boolean validateGreater(long subject, long border, String fieldToken) {
		if (subject > border) {
			return true;
		}

		add(new MustBeGreaterValidationError(subject, border, fieldToken));
		return false;
	}

	public boolean validateGreater(double subject, double border, String fieldToken) {
		if (subject > border) {
			return true;
		}

		add(new MustBeGreaterValidationError(subject, border, fieldToken));
		return false;
	}

	public boolean validateGreaterOrEqual(double subject, double border, String fieldToken) {
		if (subject >= border) {
			return true;
		}

		add(new MustBeGreaterOrEqualValidationError(subject, border, fieldToken));
		return false;
	}

	public boolean validateGreaterOrEqual(long subject, long border, String fieldToken) {
		if (subject >= border) {
			return true;
		}

		add(new MustBeGreaterOrEqualValidationError(subject, border, fieldToken));
		return false;
	}

	public boolean validateLessOrEqual(long subject, long border, String fieldToken) {
		if (subject <= border) {
			return true;
		}

		add(new MustBeLessOrEqualValidationError(subject, border, fieldToken));
		return false;
	}

	/**
	 * Convenience method for validation invoking in one line. ValidationError will
	 * be added to resulting list of errors only if any validation error found
	 * 
	 * @param validationSubject
	 * @param objectValidator
	 * @param fieldToken
	 * @return
	 */
	public <T> AggregatedObjectValidationErrors validateAggregatedObject(T validationSubject,
			ObjectValidator<T> objectValidator, String fieldToken) {
		ValidationContext ctx = new ValidationContext();
		AggregatedObjectValidationErrors aggregatedObjectValidationErrors = new AggregatedObjectValidationErrors(
				fieldToken, ctx.getErrors());

		objectValidator.validate(validationSubject, fieldToken, ctx, null, this);
		if (ctx.getHasErrors()) {
			errors.add(aggregatedObjectValidationErrors);
			return aggregatedObjectValidationErrors;
		}

		return null;
	}

	public <T> AggregatedObjectsValidationErrors validateAggregatedObjects(List<T> validationSubjects,
			ObjectValidator<T> objectValidator, String fieldToken) {

		AggregatedObjectsValidationErrors aggregatedObjectsValidationErrors = new AggregatedObjectsValidationErrors(
				fieldToken);

		for (int i = 0; i < validationSubjects.size(); i++) {
			ValidationContext ctx = new ValidationContext();
			AggregatedObjectValidationErrors aggregatedObjectValidationErrors = new AggregatedObjectValidationErrors(
					Integer.toString(i), ctx.getErrors());
			objectValidator.validate(validationSubjects.get(i), fieldToken, ctx, validationSubjects, this);
			if (ctx.getHasErrors()) {
				aggregatedObjectsValidationErrors.getAggregatedObjectValidationErrorsList()
						.add(aggregatedObjectValidationErrors);
			}
		}

		if (aggregatedObjectsValidationErrors.getHasErrors()) {
			errors.add(aggregatedObjectsValidationErrors);
			return aggregatedObjectsValidationErrors;
		}
		return null;
	}

	public boolean lengthEqOrGreater(String str, int minimumLength, String fieldToken) {
		if (str != null && str.length() >= minimumLength) {
			return true;
		}

		errors.add(new StringTooShortValidationError(fieldToken, minimumLength));
		return false;
	}

	public boolean equals(Object a, String aMessageCode, Object b, String bMessageCode, String fieldToken) {
		if (a == b || (a == null && b == null)) {
			return true;
		}
		if ((a != null && b != null) && a.equals(b)) {
			return true;
		}

		errors.add(new MustBeEqualsValidationError(aMessageCode, bMessageCode, fieldToken));
		return true;
	}

	public boolean hasText(String str, String fieldToken) {
		if (StringUtils.hasText(str)) {
			return true;
		}

		errors.add(new FieldRequiredValidationError(fieldToken));
		return false;
	}

	public boolean isTrue(boolean exprMustBeTrue, String messageCode, String fieldToken) {
		if (exprMustBeTrue) {
			return true;
		}

		errors.add(new ValidationError(messageCode, fieldToken));
		return false;
	}

	public void throwIfHasErrors() throws FieldValidationException {
		if (!errors.isEmpty()) {
			throw new FieldValidationException(errors);
		}
	}

}
