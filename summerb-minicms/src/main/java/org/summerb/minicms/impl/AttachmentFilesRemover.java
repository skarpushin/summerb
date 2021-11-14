/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.minicms.api.dto.Attachment;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * This bean will listen on an event bus (preferably tx-based event bus, so
 * after transaction is completed it will remove article's attachments if it was
 * deleted)
 * 
 * @author sergeyk
 *
 */
public class AttachmentFilesRemover implements InitializingBean {
	private Logger log = LogManager.getLogger(AttachmentFilesRemover.class);

	private EventBus eventBus;

	private String attachmentsFolder = "article-attachments";

	@Override
	public void afterPropertiesSet() throws Exception {
		eventBus.register(this);
	}

	@Subscribe
	public void onAttachmentRemoved(EntityChangedEvent<Attachment> evt) {
		if (!evt.isTypeOf(Attachment.class) || evt.getChangeType() != ChangeType.REMOVED) {
			return;
		}

		File fl = new File(attachmentsFolder + "/" + evt.getValue().getId());
		if (!fl.exists()) {
			return;
		}

		if (fl.delete()) {
			log.trace("Deleted attachment " + evt.getValue().getName() + " file " + fl.getAbsolutePath());
		} else {
			log.warn("Failed to delete attachment " + evt.getValue().getName() + " file " + fl.getAbsolutePath());
		}
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * Provide an {@link EventBus} which will be used to transmit
	 * {@link EntityChangedEvent} upon transaction completion.
	 * 
	 * @param eventBus
	 *            It could be any event bus, but it's batter to have tx-bound one so
	 *            that files are deleted only if transaction committed
	 */
	@Required
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public String getAttachmentsFolder() {
		return attachmentsFolder;
	}

	public void setAttachmentsFolder(String attachmentsFolder) {
		this.attachmentsFolder = attachmentsFolder;
	}
}
