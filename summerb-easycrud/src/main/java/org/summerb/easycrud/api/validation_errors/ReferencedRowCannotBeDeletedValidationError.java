package org.summerb.easycrud.api.validation_errors;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.validation.ValidationError;

public class ReferencedRowCannotBeDeletedValidationError extends ValidationError {
	private static final long serialVersionUID = 1187193329378440105L;

	public ReferencedRowCannotBeDeletedValidationError() {
		super("validation.referencedRowCannotBeDeleted", HasId.FN_ID);
	}

}
