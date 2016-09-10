package org.summerb.approaches.jdbccrud.impl.wireTaps;

import org.summerb.approaches.jdbccrud.api.EasyCrudWireTap;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

public class EasyCrudWireTapNoOpImpl<TId, TDto extends HasId<TId>> implements EasyCrudWireTap<TId, TDto> {

	@Override
	public boolean requiresFullDto() {
		return true;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return false;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return false;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return false;
	}

	@Override
	public void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException {
	}

	@Override
	public void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return false;
	}

	@Override
	public void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException {
	}

}
