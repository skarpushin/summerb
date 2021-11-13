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
