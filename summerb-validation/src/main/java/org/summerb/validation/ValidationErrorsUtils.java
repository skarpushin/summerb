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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public abstract class ValidationErrorsUtils {

  private ValidationErrorsUtils() {}

  public static boolean hasErrorOfType(
      @Nonnull Class<?> clazz, @Nonnull List<? extends ValidationError> errors) {
    for (ValidationError validationError : errors) {
      if (validationError.getClass().equals(clazz)) {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T> @Nullable T findErrorOfType(
      @Nonnull Class<T> clazz, @Nonnull List<? extends ValidationError> errors) {
    for (ValidationError validationError : errors) {
      if (validationError.getClass().equals(clazz)) {
        return (T) validationError;
      }
    }

    return null;
  }

  public static @Nonnull List<ValidationError> findErrorsForField(
      String propertyName, List<? extends ValidationError> errors) {
    Preconditions.checkArgument(propertyName != null, "Field token must not be null");

    List<ValidationError> ret = null;

    for (ValidationError ve : errors) {
      if (propertyName.equals(ve.getPropertyName())) {
        if (ret == null) {
          ret = new LinkedList<ValidationError>();
        }
        ret.add(ve);
      }
    }

    return ret != null ? ret : Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  public static <T> @Nullable T findErrorOfTypeForField(
      Class<T> clazz, String propertyName, List<? extends ValidationError> errors) {
    for (ValidationError validationError : errors) {
      if (!propertyName.equals(validationError.getPropertyName())) {
        continue;
      }
      if (validationError.getClass().equals(clazz)) {
        return (T) validationError;
      }
    }

    return null;
  }

  public static @Nullable ValidationErrors findAggregatedErrorsAtIndex(
      int i, @Nonnull List<ValidationError> list) {
    String idx = Integer.toString(i);
    return (ValidationErrors)
        list.stream()
            .filter(x -> x instanceof ValidationErrors && idx.equals(x.propertyName))
            .findFirst()
            .orElse(null);
  }
}
