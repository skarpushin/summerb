/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.util.CollectionUtils;
import org.summerb.utils.DtoBase;

import com.google.common.base.Preconditions;

public class ValidationErrors extends ValidationError
    implements Serializable, DtoBase, HasValidationErrors {
  private static final long serialVersionUID = -8034148535897107069L;

  protected List<ValidationError> list;

  public ValidationErrors() {
    super("validationErrors", "validation.error");
    this.list = new LinkedList<>();
  }

  public ValidationErrors(@Nonnull List<ValidationError> errors) {
    super("validationErrors", "validation.error");
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  public ValidationErrors(@Nonnull String propertyName, @Nonnull List<ValidationError> errors) {
    super(propertyName, "validation.error");
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  @Override
  public @Nonnull List<ValidationError> getList() {
    return list;
  }

  public void add(@Nonnull ValidationError validationError) {
    Preconditions.checkArgument(validationError != null, "validationError required");
    list.add(validationError);
  }

  /** @param errors errors list, must not be empty */
  public void setList(@Nonnull List<ValidationError> errors) {
    Preconditions.checkArgument(errors != null);
    this.list = errors;
  }

  @Override
  public boolean isHasErrors() {
    return !list.isEmpty();
  }

  public boolean hasErrorOfType(@Nonnull Class<?> clazz) {
    return ValidationErrorsUtils.hasErrorOfType(clazz, list);
  }

  public <V> @Nullable V findErrorOfType(@Nonnull Class<V> clazz) {
    return ValidationErrorsUtils.findErrorOfType(clazz, list);
  }

  public @Nonnull List<ValidationError> findErrorsForField(@Nonnull String propertyName) {
    return ValidationErrorsUtils.findErrorsForField(propertyName, list);
  }

  public <V> @Nullable V findErrorOfTypeForField(
      @Nonnull Class<V> clazz, @Nonnull String propertyName) {
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
  public @Nullable ValidationErrors findAggregatedErrorsAtIndex(int i) {
    return ValidationErrorsUtils.findAggregatedErrorsAtIndex(i, list);
  }

  @Override
  public String toString() {
    return toString(this, propertyName);
  }

  public static String toString(@Nonnull HasValidationErrors errors, @Nullable String prefix) {
    if (CollectionUtils.isEmpty(errors.getList())) {
      return prefix + ": (empty)";
    }

    StringBuilder ret = new StringBuilder();
    ret.append(prefix).append(": ");

    Iterator<ValidationError> iter = errors.getList().iterator();
    while (iter.hasNext()) {
      ValidationError ve = iter.next();
      ret.append("\n\t");
      ret.append(ve.toString().replaceAll("\n\t", "\n\t\t"));
    }

    return ret.toString();
  }
}
