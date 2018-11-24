package org.summerb.microservices.articles.impl;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.AttachmentDao;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Attachment;

public class AttachmentServiceImpl extends EasyCrudServicePluggableImpl<Long, Attachment, AttachmentDao>
		implements AttachmentService {
	private static Attachment[] attachmentArrayType = new Attachment[0];

	public AttachmentServiceImpl() {
		setDtoClass(Attachment.class);
		setEntityTypeMessageCode("term.articles.attachment");

		// Legacy:
		setWireTap(new EasyCrudWireTapValidationImpl<>(new AttachmentValidationStrategyImpl()));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Attachment create(Attachment dto) throws FieldValidationException, NotAuthorizedException {
		try {
			return super.create(dto);
		} finally {
			if (dto != null && dto.getContents() != null) {
				try {
					dto.getContents().close();
				} catch (IOException e) {
					// don't care
				}
			}
		}
	}

	@Override
	public InputStream getContentInputStream(long id) throws NotAuthorizedException {
		try {
			return dao.getContentInputStream(id);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public Attachment[] findArticleAttachments(long articleId) throws NotAuthorizedException {
		PaginatedList<Attachment> results = query(PagerParams.ALL, Query.n().eq(Attachment.FN_ARTICLE_ID, articleId),
				OrderBy.Asc(Attachment.FN_NAME));
		return results.getItems().toArray(attachmentArrayType);
	}

}
