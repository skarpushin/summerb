package org.summerb.approaches.validation;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;

public class ValidationErrorsUtils {
	public static boolean hasErrorOfType(Class<?> clazz, List<ValidationError> errors) {
		for (ValidationError validationError : errors) {
			if (validationError.getClass().equals(clazz)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> T findErrorOfType(Class<T> clazz, List<ValidationError> errors) {
		for (ValidationError validationError : errors) {
			if (validationError.getClass().equals(clazz)) {
				return (T) validationError;
			}
		}

		return null;
	}

	public static List<ValidationError> findErrorsForField(String fieldToken, List<ValidationError> errors) {
		Preconditions.checkArgument(fieldToken != null, "Field token must not be null");

		List<ValidationError> ret = new LinkedList<ValidationError>();

		for (ValidationError ve : errors) {
			if (fieldToken.equals(ve.getFieldToken())) {
				ret.add(ve);
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> T findErrorOfTypeForField(Class<T> clazz, String fieldToken, List<ValidationError> errors) {
		for (ValidationError validationError : errors) {
			if (!fieldToken.equals(validationError.getFieldToken())) {
				continue;
			}
			if (validationError.getClass().equals(clazz)) {
				return (T) validationError;
			}
		}

		return null;
	}
}
