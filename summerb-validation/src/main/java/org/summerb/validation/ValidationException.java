/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import java.io.Serial;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.exceptions.HasErrorDescriptionObject;

/**
 * @author sergey.karpushin
 */
public class ValidationException extends RuntimeException
    implements HasMessageCode, HasErrorDescriptionObject<ValidationErrors> {
  @Serial private static final long serialVersionUID = -310812271204903287L;

  public static final String MESSAGE_CODE = "validation.error";

  protected ValidationErrors errors;

  /**
   * @deprecated for io purposes only
   */
  @Deprecated
  public ValidationException() {}

  public ValidationException(ValidationError validationError) {
    Preconditions.checkArgument(validationError != null, "validationError required");
    this.errors = new ValidationErrors(MESSAGE_CODE, List.of(validationError));
  }

  public ValidationException(List<ValidationError> validationErrors) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(validationErrors), "validationErrors required");
    this.errors = new ValidationErrors(MESSAGE_CODE, validationErrors);
  }

  public ValidationException(ValidationErrors validationErrors) {
    Preconditions.checkArgument(
        validationErrors != null && !CollectionUtils.isEmpty(validationErrors.getList()),
        "validationErrors required");
    this.errors = validationErrors;
  }

  public List<ValidationError> getErrors() {
    return errors.getList();
  }

  public boolean hasErrorOfType(Class<?> clazz) {
    return ValidationErrorsUtils.hasErrorOfType(clazz, errors.getList());
  }

  public <V> V findErrorOfType(Class<V> clazz) {
    return ValidationErrorsUtils.findErrorOfType(clazz, errors.getList());
  }

  public List<ValidationError> findErrorsForField(String propertyName) {
    return ValidationErrorsUtils.findErrorsForField(propertyName, errors.getList());
  }

  public <V> V findErrorOfTypeForField(Class<V> clazz, String propertyName) {
    return ValidationErrorsUtils.findErrorOfTypeForField(clazz, propertyName, errors.getList());
  }

  @Override
  public String getMessageCode() {
    return MESSAGE_CODE;
  }

  @Override
  public String toString() {
    return errors.toString();
  }

  @Override
  public ValidationErrors getErrorDescriptionObject() {
    return errors;
  }
}
