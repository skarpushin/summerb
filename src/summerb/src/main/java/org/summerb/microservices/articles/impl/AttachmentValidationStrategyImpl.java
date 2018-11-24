package org.summerb.microservices.articles.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.microservices.articles.api.dto.Attachment;

public class AttachmentValidationStrategyImpl implements EasyCrudValidationStrategy<Attachment> {
	@Override
	public void validateForUpdate(Attachment existingVersion, Attachment newVersion) throws FieldValidationException {
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
}
