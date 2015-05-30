package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudValidationStrategy;

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
