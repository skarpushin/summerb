/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.EasyCrudValidationStrategyAbstract;
import org.summerb.validation.FieldValidationException;

/**
 * Strategy interface that has method for validating DTO before it will be
 * created and updated.
 * 
 * <p>
 * 
 * Normally will be injected into {@link EasyCrudService}. In case of using
 * {@link EasyCrudServicePluggableImpl} via {@link EasyCrudWireTap}, but also
 * impl of this interface can be used separately
 * 
 * <p>
 * 
 * In case validation rules are the same for both
 * {@link EasyCrudService#create(Object)} and
 * {@link EasyCrudService#update(Object)}, consider sub-classing
 * {@link EasyCrudValidationStrategyAbstract}
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudValidationStrategy<TDto> {
	void validateForCreate(TDto dto) throws FieldValidationException;

	void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException;
}
