package org.summerb.approaches.validation;

import java.io.Serializable;
import java.util.List;

import org.summerb.utils.DtoBase;

public class ValidationErrors implements Serializable, DtoBase {
	private static final long serialVersionUID = -8034148535897107069L;

	private List<ValidationError> errors;

	public ValidationErrors() {
	}

	public ValidationErrors(List<ValidationError> errors) {
		this.errors = errors;
	}

	public List<ValidationError> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationError> errors) {
		this.errors = errors;
	}
}
