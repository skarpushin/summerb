package org.summerb.easycrud.api.validation_errors;

import org.summerb.validation.ValidationError;

public class ReferencedRowNotFoundValidationError extends ValidationError {
	private static final long serialVersionUID = -6942605514588612619L;

	public ReferencedRowNotFoundValidationError(String fieldName) {
		super(fieldName, "validation.referencedObjectNotFound");
	}
}
