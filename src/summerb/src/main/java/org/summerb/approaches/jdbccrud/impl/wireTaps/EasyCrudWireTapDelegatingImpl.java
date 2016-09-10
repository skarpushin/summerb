package org.summerb.approaches.jdbccrud.impl.wireTaps;

import java.util.List;

import org.summerb.approaches.jdbccrud.api.EasyCrudWireTap;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;

public class EasyCrudWireTapDelegatingImpl<TId, TDto extends HasId<TId>> implements EasyCrudWireTap<TId, TDto> {
	private List<EasyCrudWireTap<TId, TDto>> chain;

	public EasyCrudWireTapDelegatingImpl(List<EasyCrudWireTap<TId, TDto>> chain) {
		Preconditions.checkArgument(chain != null, "chain list must not be null");
		this.chain = chain;
	}

	@Override
	public boolean requiresFullDto() {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			if (tap.requiresFullDto()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			if (tap.requiresOnCreate()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.beforeCreate(dto);
		}
	}

	@Override
	public void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.afterCreate(dto);
		}
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			if (tap.requiresOnUpdate()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.beforeUpdate(from, to);
		}
	}

	@Override
	public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.afterUpdate(from, to);
		}
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			if (tap.requiresOnDelete()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.beforeDelete(dto);
		}
	}

	@Override
	public void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.afterDelete(dto);
		}
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			if (tap.requiresOnRead()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException {
		for (EasyCrudWireTap<TId, TDto> tap : chain) {
			tap.afterRead(dto);
		}
	}

}
