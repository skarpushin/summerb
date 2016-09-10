package org.summerb.microservices.articles.impl;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServiceTableAuthImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.microservices.articles.api.AttachmentDao;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Attachment;

public class AttachmentServiceImpl extends EasyCrudServiceTableAuthImpl<Long, Attachment>implements AttachmentService {

	public AttachmentServiceImpl() {
		setDtoClass(Attachment.class);
		setEntityTypeMessageCode("term.articles.attachment");
		setValidationStrategy(validationStrategy);
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

	private EasyCrudValidationStrategy<Attachment> validationStrategy = new EasyCrudValidationStrategy<Attachment>() {
		@Override
		public void validateForUpdate(Attachment existingVersion, Attachment newVersion)
				throws FieldValidationException {
			throw new RuntimeException("Update operation is not supposed for attachments");
		}

		@Override
		public void validateForCreate(Attachment dto) throws FieldValidationException {
			ValidationContext ctx = new ValidationContext();

			if (ctx.validateNotEmpty(dto.getName(), Attachment.FN_NAME)) {
				ctx.validateDataLengthLessOrEqual(dto.getName(), Attachment.FN_NAME_MAXSIZE, Attachment.FN_NAME);
			}
			ctx.validateNotEmpty(dto.getArticleId(), Attachment.FN_ARTICLE_ID);
			ctx.validateNotEmpty(dto.getSize(), Attachment.FN_SIZE);
			ctx.validateNotNull(dto.getContents(), Attachment.FN_CONTENTS);

			if (ctx.getHasErrors()) {
				throw new FieldValidationException(ctx.getErrors());
			}
		}
	};

	@Override
	public InputStream getContentInputStream(long id) throws NotAuthorizedException {
		try {
			AttachmentDao ourDao = (AttachmentDao) dao;
			return ourDao.getContentInputStream(id);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

}
