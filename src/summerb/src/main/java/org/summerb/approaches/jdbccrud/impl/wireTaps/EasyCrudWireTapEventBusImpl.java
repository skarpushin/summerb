package org.summerb.approaches.jdbccrud.impl.wireTaps;

import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

public class EasyCrudWireTapEventBusImpl<TId, TDto extends HasId<TId>> extends EasyCrudWireTapNoOpImpl<TId, TDto> {
	private EventBus eventBus;

	public EasyCrudWireTapEventBusImpl(EventBus eventBus) {
		Preconditions.checkArgument(eventBus != null);
		this.eventBus = eventBus;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException {
		eventBus.post(EntityChangedEvent.added(dto));
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException {
		eventBus.post(EntityChangedEvent.updated(to));
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException {
		eventBus.post(EntityChangedEvent.removedObject(dto));
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return false;
	}

}
