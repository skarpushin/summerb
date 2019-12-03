/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.minicms.impl;

import java.io.InputStream;
import java.io.NotSerializableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.objectcopy.DeepCopy;
import org.summerb.validation.FieldValidationException;

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
