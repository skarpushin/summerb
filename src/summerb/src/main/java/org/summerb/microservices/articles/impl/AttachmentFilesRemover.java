package org.summerb.microservices.articles.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.microservices.articles.api.dto.Attachment;

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
	private Logger log = Logger.getLogger(AttachmentFilesRemover.class);

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
	 * @param eventBus It could be any event bus, but it's batter to have tx-bound
	 *                 one so that files are deleted only if transaction committed
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
