package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.validation.FieldValidationException;
import org.summerb.validation.ValidationContext;

public abstract class EasyCrudValidationStrategyAbstract<TDto> implements EasyCrudValidationStrategy<TDto> {
	@Override
	public void validateForCreate(TDto dto) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		doValidateForCreate(dto, ctx);

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}

	protected abstract void doValidateForCreate(TDto dto, ValidationContext ctx);

	@Override
	public void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		doValidateForUpdate(existingVersion, newVersion, ctx);

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}

	protected void doValidateForUpdate(TDto existingVersion, TDto newVersion, ValidationContext ctx) {
		doValidateForCreate(newVersion, ctx);
	};

}
