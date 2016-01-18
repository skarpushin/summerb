package org.summerb.approaches.jdbccrud.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;

public class EasyCrudValidationStrategyNoOpImpl<TDto> implements EasyCrudValidationStrategy<TDto> {

	@Override
	public void validateForCreate(TDto dto) {
		// no impl
	}

	@Override
	public void validateForUpdate(TDto existingVersion, TDto newVersion) {
		// no impl
	}

}
