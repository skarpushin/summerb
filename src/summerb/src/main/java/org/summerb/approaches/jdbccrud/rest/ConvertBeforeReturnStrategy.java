package org.summerb.approaches.jdbccrud.rest;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;

/**
 * Use this to alter response right before it gets returned to consumer
 */
public class ConvertBeforeReturnStrategy<TId, TDto extends HasId<TId>> {
	public MultipleItemsResult<TId, TDto> convert(MultipleItemsResult<TId, TDto> ret) {
		return ret;
	}

	public SingleItemResult<TId, TDto> convert(SingleItemResult<TId, TDto> ret) {
		return ret;
	}
}