package org.summerb.easycrud.api.validation_errors;

import java.io.Serial;
import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.validation.ValidationError;

public class ReferencedRowCannotBeDeletedValidationError extends ValidationError {
  @Serial private static final long serialVersionUID = 1187193329378440105L;

  public ReferencedRowCannotBeDeletedValidationError() {
    super(HasId.FN_ID, EasyCrudMessageCodes.VALIDATION_REFERENCED_ROW_CANNOT_BE_DELETED);
  }
}
