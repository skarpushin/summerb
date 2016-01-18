package org.summerb.approaches.validation;

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
	 *            name of the field which references custom object (not just
	 *            simple scalar variable) which was validated and contains
	 *            errors. In case if this DTO is used to represent object in
	 *            collection, this field might be used to identify collection
	 *            item instance (it might be item idx, guid or any other thing
	 *            suitable for end-application)
	 * @param aggregatedObjectValidationErrorsList
	 *            list of containers, which represent validation errors for
	 *            specific items from collection
	 */
	@SuppressWarnings("deprecation")
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
