package org.summerb.approaches.jdbccrud.impl.wireTaps;

import org.summerb.approaches.jdbccrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;

/**
 * WireTap which will invoke injected {@link EasyCrudPerRowAuthStrategy} whn
 * accessing or modifying data.
 * 
 * @author sergeyk
 */
public class EasyCrudWireTapPerRowAuthImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EasyCrudPerRowAuthStrategy<TDto> strategy;

	public EasyCrudWireTapPerRowAuthImpl(EasyCrudPerRowAuthStrategy<TDto> strategy) {
		Preconditions.checkArgument(strategy != null);
		this.strategy = strategy;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToCreate(dto);
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToUpdate(from, to);
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException {
		strategy.assertAuthorizedToDelete(dto);
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException {
		strategy.assertAuthorizedToRead(dto);
	}

}
