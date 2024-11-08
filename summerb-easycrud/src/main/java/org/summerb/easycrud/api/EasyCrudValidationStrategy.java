/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.api;

import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.validation.EasyCrudValidationStrategyAbstract;
import org.summerb.validation.ValidationException;

/**
 * Strategy interface that has method for validating DTO before it will be created and updated.
 *
 * <p>Normally will be injected into {@link EasyCrudService}. In case of using {@link
 * EasyCrudServiceImpl} via {@link EasyCrudWireTap}, but also impl of this interface can be used
 * separately
 *
 * <p>In case validation rules are the same for both {@link
 * EasyCrudService#create(org.summerb.easycrud.api.row.HasId)} and {@link
 * EasyCrudService#update(org.summerb.easycrud.api.row.HasId)}, consider sub-classing {@link
 * EasyCrudValidationStrategyAbstract}
 *
 * @author sergey.karpushin
 */
public interface EasyCrudValidationStrategy<TRow> {

  /**
   * @param row row to validate
   * @throws ValidationException in case there are validation errors
   */
  void validateForCreate(TRow row);

  /**
   * This method tells EasyCrud how to validate for update (method {@link #validateForUpdate(Object,
   * Object)})
   *
   * @return Return true if validation logic require currently persisted row version to be passed to
   *     {@link #validateForUpdate(Object, Object)}. Return false if only new version of the row is
   *     needed to perform validation
   */
  boolean isCurrentlyPersistedRowNeededForUpdateValidation();

  /**
   * @param existingVersion currently persisted row version for reference (in some cases validation
   *     rules might depend on previous state of the row)
   * @param newVersion row to validate
   * @throws ValidationException in case there are validation errors
   */
  void validateForUpdate(TRow existingVersion, TRow newVersion);
}
