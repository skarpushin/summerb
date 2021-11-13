/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

/**
 * Validation error which acts as a container for collection of objects which
 * were validated
 *
 * @author sergey.karpushin
 *
 */
public class AggregatedObjectsValidationErrors extends ValidationError implements HasValidationErrors {
	private static final long serialVersionUID = -1988047599025239239L;

	private List<AggregatedObjectValidationErrors> aggregatedObjectValidationErrorsList;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public AggregatedObjectsValidationErrors() {
	}

	/**
	 *
	 * @param fieldToken
	 *            name of the field which references custom object (not just simple
	 *            scalar variable) which was validated and contains errors. In case
	 *            if this DTO is used to represent object in collection, this field
	 *            might be used to identify collection item instance (it might be
	 *            item idx, guid or any other thing suitable for end-application)
	 * @param aggregatedObjectValidationErrors
	 *            list of containers, which represent validation errors for specific
	 *            items from collection
	 */
	public AggregatedObjectsValidationErrors(String fieldToken,
			List<AggregatedObjectValidationErrors> aggregatedObjectValidationErrors) {
		super("validation.aggregatedObjectsError", fieldToken);
		Preconditions.checkArgument(aggregatedObjectValidationErrors != null,
				"aggregatedObjectValidationErrorsList must not be null");
		this.aggregatedObjectValidationErrorsList = aggregatedObjectValidationErrors;
	}

	public AggregatedObjectsValidationErrors(String fieldToken) {
		this(fieldToken, new LinkedList<AggregatedObjectValidationErrors>());
	}

	// @Transient TBD: Explain why I made it Transient?
	// @JsonIgnore TBD: Explain why I made it Transient?
	public List<AggregatedObjectValidationErrors> getAggregatedObjectValidationErrorsList() {
		return aggregatedObjectValidationErrorsList;
	}

	public void setAggregatedObjectValidationErrorsList(
			List<AggregatedObjectValidationErrors> aggregatedObjectValidationErrorsList) {
		this.aggregatedObjectValidationErrorsList = aggregatedObjectValidationErrorsList;
	}

	@Override
	public boolean getHasErrors() {
		return aggregatedObjectValidationErrorsList.size() > 0;
	}

	@Override
	public List<? extends ValidationError> getValidationErrors() {
		return aggregatedObjectValidationErrorsList;
	}

	public AggregatedObjectValidationErrors findErrorsForFieldToken(String fieldToken) {
		for (AggregatedObjectValidationErrors ve : aggregatedObjectValidationErrorsList) {
			if (fieldToken.equals(ve.getFieldToken())) {
				return ve;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		if (CollectionUtils.isEmpty(aggregatedObjectValidationErrorsList)) {
			return super.toString() + " (empty)";
		}

		StringBuilder ret = new StringBuilder();

		for (ValidationError ve : aggregatedObjectValidationErrorsList) {
			if (ret.length() > 0) {
				ret.append("; ");
			}

			ret.append(ve.toString());
		}

		return getClass().getSimpleName() + " (field = '" + getFieldToken() + "'): " + ret.toString();
	}

}
