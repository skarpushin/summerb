package org.summerb.approaches.jdbccrud.impl.wireTaps;

import org.summerb.approaches.jdbccrud.api.EasyCrudTableAuthStrategy;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;

public class EasyCrudWireTapTableAuthImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EasyCrudTableAuthStrategy strategy;

	public EasyCrudWireTapTableAuthImpl(EasyCrudTableAuthStrategy strategy) {
		Preconditions.checkArgument(strategy != null);
		this.strategy = strategy;
	}

	@Override
	public boolean requiresFullDto() {
		return false;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToCreate();
		return false;
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToUpdate();
		return false;
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToDelete();
		return false;
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToRead();
		return false;
	}

}
