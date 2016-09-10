package org.summerb.approaches.jdbccrud.impl.wireTaps;

import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;

public class EasyCrudWireTapValidationImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EasyCrudValidationStrategy<TDto> strategy;

	public EasyCrudWireTapValidationImpl(EasyCrudValidationStrategy<TDto> strategy) {
		Preconditions.checkArgument(strategy != null);
		this.strategy = strategy;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
		strategy.validateForCreate(dto);
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
		strategy.validateForUpdate(from, to);
	}

}
