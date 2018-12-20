package org.summerb.approaches.jdbccrud.rest;

import java.util.List;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;

/**
 * Use this to alter response right before it gets returned to consumer
 */
public class ConvertBeforeReturnStrategy<TId, TDto extends HasId<TId>> {
	protected boolean isConvertionRequired() {
		return false;
	}

	public MultipleItemsResult<TId, TDto> convert(MultipleItemsResult<TId, TDto> ret) {
		if (!isConvertionRequired()) {
			return ret;
		}
		List<TDto> rows = ret.getRows();
		for (int i = 0; i < rows.size(); i++) {
			rows.set(i, convert(rows.get(i)));
		}
		return ret;
	}

	public SingleItemResult<TId, TDto> convert(SingleItemResult<TId, TDto> ret) {
		if (!isConvertionRequired()) {
			return ret;
		}
		ret.setRow(convert(ret.getRow()));
		return ret;
	}

	protected TDto convert(TDto row) {
		return row;
	}
}