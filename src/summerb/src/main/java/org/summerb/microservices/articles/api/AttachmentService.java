package org.summerb.microservices.articles.api;

import java.io.InputStream;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.microservices.articles.api.dto.Attachment;

public interface AttachmentService extends EasyCrudService<Long, Attachment> {
	InputStream getContentInputStream(long id) throws NotAuthorizedException;
}
