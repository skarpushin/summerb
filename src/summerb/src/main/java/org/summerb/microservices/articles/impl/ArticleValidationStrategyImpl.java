package org.summerb.microservices.articles.impl;

import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.microservices.articles.api.dto.Article;

public class ArticleValidationStrategyImpl implements EasyCrudValidationStrategy<Article> {
	@Override
	public void validateForCreate(Article dto) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		if (ctx.validateNotEmpty(dto.getArticleKey(), Article.FN_KEY)) {
			ctx.validateDataLengthLessOrEqual(dto.getArticleKey(), Article.FN_KEY_SIZE, Article.FN_KEY);
		}
		if (StringUtils.hasText(dto.getLang())) {
			ctx.validateDataLengthLessOrEqual(dto.getLang(), Article.FN_LANG_SIZE, Article.FN_LANG);
		}
		if (ctx.validateNotEmpty(dto.getTitle(), Article.FN_TITLE)) {
			ctx.validateDataLengthLessOrEqual(dto.getTitle(), Article.FN_TITLE_SIZE, Article.FN_TITLE);
		}
		if (StringUtils.hasText(dto.getAnnotation())) {
			ctx.validateDataLengthLessOrEqual(dto.getAnnotation(), Article.FN_ANNOTATION_SIZE, Article.FN_ANNOTATION);
		}
		if (ctx.validateNotEmpty(dto.getContent(), Article.FN_CONTENT)) {
			ctx.validateDataLengthLessOrEqual(dto.getContent(), Article.FN_CONTENT_SIZE, Article.FN_CONTENT);
		}
		if (StringUtils.hasText(dto.getArticleGroup())) {
			ctx.validateDataLengthLessOrEqual(dto.getArticleGroup(), Article.FN_GROUP_SIZE, Article.FN_GROUP);
		}

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}

	@Override
	public void validateForUpdate(Article existingVersion, Article newVersion) throws FieldValidationException {
		validateForCreate(newVersion);
	}
}
