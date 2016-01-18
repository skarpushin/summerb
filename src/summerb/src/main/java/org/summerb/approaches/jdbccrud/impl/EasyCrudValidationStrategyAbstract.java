package org.summerb.approaches.jdbccrud.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;

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
