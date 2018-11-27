package org.summerb.microservices.articles.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

/**
 * This WireTap is usefull when we either need to monitor cascade attachment
 * deletions on event bus, or we're using {@link AttachmentDaoExtFilesImpl} and
 * we need to make sure there will be process in place which removes actual
 * files from disk (you supposed to register {@link AttachmentFilesRemover} bean
 * and you have pre-commit event bus and post-commit event bus.
 * 
 * @see com.google.common.eventbus.AsyncEventBus
 * @see org.summerb.utils.tx.AfterCommitExecutorThreadLocalImpl
 * 
 * @author sergeyk
 *
 */
public class ArticleAttachmentRemoverWireTap extends EasyCrudWireTapNoOpImpl<Long, Article> {
	@Autowired
	private AttachmentService attachmentService;

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return true;
	}

	@Override
	public boolean requiresFullDto() {
		return false;
	}

	@Override
	public void beforeDelete(Article dto) throws FieldValidationException, NotAuthorizedException {
		attachmentService.deleteByQuery(Query.n().eq(Attachment.FN_ARTICLE_ID, dto.getId()));
	}
}
