package org.summerb.microservices.articles.impl;

import java.io.InputStream;
import java.io.NotSerializableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.dto.Attachment;
import org.summerb.utils.DeepCopy;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * a modification of {@link EasyCrudWireTapEventBusImpl} that will essentially
 * remove {@link InputStream} if any from {@link Attachment#getContents()}
 * before sending into EventBus. It's important because messages often will be
 * serialized in some form to be written on disk or sent over the network
 * 
 * @author sergeyk
 */
public class AttachmentEventBusWireTapImpl extends EasyCrudWireTapNoOpImpl<Long, Attachment> {
	private EventBus eventBus;

	@Autowired
	public AttachmentEventBusWireTapImpl(EventBus eventBus) {
		Preconditions.checkArgument(eventBus != null);
		this.eventBus = eventBus;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void afterCreate(Attachment dto) throws FieldValidationException, NotAuthorizedException {
		eventBus.post(EntityChangedEvent.added(getDtoSafeForEventBus(dto)));
	}

	private Attachment getDtoSafeForEventBus(Attachment dto) {
		try {
			if (dto.getContents() == null) {
				return dto;
			}
			InputStream contents = dto.getContents();
			dto.setContents(null);
			Attachment dto2 = DeepCopy.copyOrPopagateExcIfAny(dto);
			dto.setContents(contents);
			return dto2;
		} catch (NotSerializableException e) {
			throw new RuntimeException("Failed to identify the version of the Attachment DTO that is safe for eventBus",
					e);
		}
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return true;
	}

	@Override
	public void afterUpdate(Attachment from, Attachment to) throws NotAuthorizedException, FieldValidationException {
		eventBus.post(EntityChangedEvent.updated(getDtoSafeForEventBus(to)));
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public void afterDelete(Attachment dto) throws FieldValidationException, NotAuthorizedException {
		eventBus.post(EntityChangedEvent.removedObject(getDtoSafeForEventBus(dto)));
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return false;
	}
}
