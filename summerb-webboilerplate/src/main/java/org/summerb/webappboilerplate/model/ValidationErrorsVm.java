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
package org.summerb.webappboilerplate.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.summerb.i18n.I18nUtils;
import org.summerb.utils.collection.DummyMapImpl;
import org.summerb.validation.ValidationError;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

/**
 * View model for validation errors list.
 * 
 * It's designed to be added to {@link Model} and used in JSP
 * 
 * @author sergey.karpushin
 * 
 */
public class ValidationErrorsVm {
	private Logger log = LogManager.getLogger(getClass());

	private final List<ValidationError> validationErrors;
	private Map<String, String> errorsMap;

	private Map<String, String> hasError = new DummyMapImpl<String, String>() {
		@Override
		public String get(Object key) {
			// NOTE: This "error" literal is actually a css class name. This way
			// it's very handy to put it into jsp file
			return getMsg().get(key) != null ? "has-error" : "";
		}
	};

	private Map<String, String> hideCssClass = new DummyMapImpl<String, String>() {
		@Override
		public String get(Object key) {
			// NOTE: This "error" literal is actually a css class name. This way
			// it's very handy to put it into jsp file
			return getMsg().get(key) != null ? "" : "hide";
		}
	};

	public ValidationErrorsVm(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public ValidationErrorsVm(ValidationError validationError) {
		validationErrors = new LinkedList<ValidationError>();
		validationErrors.add(validationError);
	}

	/**
	 * @return Map that maps field token to error msg. Message will be null if there
	 *         is no error for that field.
	 */
	public Map<String, String> getMsg() {
		if (errorsMap == null) {
			errorsMap = new HashMap<String, String>();
			for (ValidationError validationError : validationErrors) {
				try {
					String message = I18nUtils.buildMessage(validationError, CurrentRequestUtils.getWac(),
							LocaleContextHolder.getLocale());

					// CHeck if there is already at least one message for that
					// field. Concatenate errors.
					String existing = errorsMap.get(validationError.getFieldToken());
					String newMessageValue = "";
					if (StringUtils.hasText(existing)) {
						if (existing.endsWith(".")) {
							newMessageValue = existing + " " + message;
						} else {
							newMessageValue = existing + ". " + message;
						}
					} else {
						newMessageValue = message;
					}

					errorsMap.put(validationError.getFieldToken(), newMessageValue);
				} catch (Throwable t) {
					// don't really care
					log.warn("Failed to get field validation error message", t);
				}
			}
		}

		return errorsMap;
	}

	/**
	 * @return Map that maps field token to error result. Error result is an empty
	 *         string if there is no error and "error" if there is an error for that
	 *         field (field is a key for this map)
	 */
	public Map<String, String> getHas() {
		return hasError;
	}

	public Map<String, String> getShowIf() {
		return hideCssClass;
	}

}
