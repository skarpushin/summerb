package org.summerb.easycrud.validation.errors;

import java.io.Serial;
import org.summerb.easycrud.EasyCrudMessageCodes;
import org.summerb.validation.ValidationError;

public class ReferencedRowNotFoundValidationError extends ValidationError {
  @Serial private static final long serialVersionUID = -6942605514588612619L;

  public ReferencedRowNotFoundValidationError(String fieldName) {
    super(fieldName, EasyCrudMessageCodes.VALIDATION_REFERENCED_OBJECT_NOT_FOUND);
  }
}
