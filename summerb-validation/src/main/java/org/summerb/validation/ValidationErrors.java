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
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.summerb.utils.DtoBase;

public class ValidationErrors extends ValidationError
    implements Serializable, DtoBase, HasValidationErrors {
  @Serial private static final long serialVersionUID = -8034148535897107069L;

  protected List<ValidationError> list;

  public ValidationErrors() {
    super("validationErrors", "validation.error");
    this.list = new LinkedList<>();
  }

  public ValidationErrors(List<ValidationError> errors) {
    super("validationErrors", "validation.error");
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  public ValidationErrors(String propertyName, List<ValidationError> errors) {
    super(propertyName, "validation.error");
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  @Override
  public List<ValidationError> getList() {
    return list;
  }

  public void add(ValidationError validationError) {
    Preconditions.checkArgument(validationError != null, "validationError required");
    list.add(validationError);
  }

  /**
   * @param errors errors list, must not be empty
   */
  public void setList(List<ValidationError> errors) {
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  @Override
  public boolean isHasErrors() {
    return !list.isEmpty();
  }

  public boolean hasErrorOfType(Class<?> clazz) {
    return ValidationErrorsUtils.hasErrorOfType(clazz, list);
  }

  public <V> V findErrorOfType(Class<V> clazz) {
    return ValidationErrorsUtils.findErrorOfType(clazz, list);
  }

  public List<ValidationError> findErrorsForField(String propertyName) {
    return ValidationErrorsUtils.findErrorsForField(propertyName, list);
  }

  public <V> V findErrorOfTypeForField(Class<V> clazz, String propertyName) {
    return ValidationErrorsUtils.findErrorOfTypeForField(clazz, propertyName, list);
  }

  /**
   * This method will be useful in situation when you've previously invoked {@link
   * ValidationContext#validateCollection(java.util.function.Function, ObjectValidator)} and now you
   * want to find validation errors for object which was found at index <code>i</code> in the
   * original list
   *
   * @param i index of an element in the original collection
   * @return instance of {@link ValidationErrors} or null if no errors
   */
  public ValidationErrors findAggregatedErrorsAtIndex(int i) {
    return ValidationErrorsUtils.findAggregatedErrorsAtIndex(i, list);
  }

  @Override
  public String toString() {
    return toString(this, propertyName);
  }

  public static String toString(HasValidationErrors errors, String prefix) {
    if (CollectionUtils.isEmpty(errors.getList())) {
      return prefix + ": (empty)";
    }

    StringBuilder ret = new StringBuilder();
    ret.append(prefix).append(": ");

    for (ValidationError ve : errors.getList()) {
      ret.append("\n\t");
      ret.append(ve.toString().replaceAll("\n\t", "\n\t\t"));
    }

    return ret.toString();
  }
}
