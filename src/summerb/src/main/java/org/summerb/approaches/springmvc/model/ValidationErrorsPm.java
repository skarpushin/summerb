package org.summerb.approaches.springmvc.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.summerb.approaches.i18n.I18nUtils;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.approaches.springmvc.utils.DummyMapImpl;
import org.summerb.approaches.validation.ValidationError;

/**
 * Presentation model for validation errors list.
 * 
 * TODO: Think on how this class may be made mmore lightweight and not
 * requireing creation of so many objects
 * 
 * @author sergey.karpushin
 * 
 */
public class ValidationErrorsPm {
	private Logger log = Logger.getLogger(getClass());

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

	public ValidationErrorsPm(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public ValidationErrorsPm(ValidationError validationError) {
		validationErrors = new LinkedList<ValidationError>();
		validationErrors.add(validationError);
	}

	/**
	 * @return Map that maps field token to error msg. Message will be null if
	 *         there is no error for that field.
	 */
	public Map<String, String> getMsg() {
		if (errorsMap == null) {
			errorsMap = new HashMap<String, String>();
			for (ValidationError validationError : validationErrors) {
				try {
					String message = I18nUtils.buildMessage(validationError, CurrentRequestUtils.getWac());

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
	 * @return Map that maps field token to error result. Error result is an
	 *         empty string if there is no error and "error" if there is an
	 *         error for that field (field is a key for this map)
	 */
	public Map<String, String> getHas() {
		return hasError;
	}

	public Map<String, String> getShowIf() {
		return hideCssClass;
	}

}
