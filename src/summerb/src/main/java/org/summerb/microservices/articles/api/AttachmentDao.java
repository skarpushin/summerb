package org.summerb.microservices.articles.api;

import java.io.InputStream;

import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.microservices.articles.api.dto.Attachment;

public interface AttachmentDao extends EasyCrudDao<Long, Attachment> {
	InputStream getContentInputStream(long id);

	void putContentInputStream(long id, InputStream contents);
}
