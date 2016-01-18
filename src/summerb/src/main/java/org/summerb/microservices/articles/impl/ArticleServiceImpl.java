package org.summerb.microservices.articles.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.EasyCrudExceptionStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.common.DaoExceptionUtils;
import org.summerb.approaches.jdbccrud.impl.EasyCrudExceptionStrategyDefaultImpl;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServiceSimpleAuthImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.approaches.validation.errors.DuplicateNameValidationError;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

import com.google.common.base.Preconditions;

public class ArticleServiceImpl extends EasyCrudServiceSimpleAuthImpl<Long, Article>implements ArticleService {
	private AttachmentService attachmentService;
	private static Attachment[] attachmentArrayType = new Attachment[0];
	private Locale fallbackToLocale = new Locale("en");

	public ArticleServiceImpl() {
		setDtoClass(Article.class);
		setEntityTypeMessageCode("term.articles.article");
		setGenericExceptionStrategy(easyCrudExceptionStrategy);
		setValidationStrategy(validationStrategy);
	}

	private EasyCrudValidationStrategy<Article> validationStrategy = new EasyCrudValidationStrategy<Article>() {
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
				ctx.validateDataLengthLessOrEqual(dto.getAnnotation(), Article.FN_ANNOTATION_SIZE,
						Article.FN_ANNOTATION);
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
	};

	private EasyCrudExceptionStrategy<Long> easyCrudExceptionStrategy = new EasyCrudExceptionStrategyDefaultImpl<Long>(
			"term.articles.article") {
		@Override
		protected RuntimeException buildUnexpectedAtCreate(Throwable t) throws FieldValidationException {
			if (t instanceof DuplicateKeyException) {
				DuplicateKeyException dke = (DuplicateKeyException) t;
				String violatedConstraintName = DaoExceptionUtils.findViolatedConstraintName(dke);

				// Handle case when uuid is duplicated
				if (DaoExceptionUtils.CONSTRAINT_PRIMARY.equals(violatedConstraintName)) {
					throw new IllegalArgumentException("Article with same id already exists", dke);
				}

				// Handle case when locale is duplicated within version
				if ("articles_UNIQUE".equals(violatedConstraintName)) {
					throw new FieldValidationException(new DuplicateNameValidationError(Article.FN_KEY));
				}
			}

			return super.buildUnexpectedAtCreate(t);
		}
	};

	@Override
	public Article findArticleByKeyAndLocale(String key, Locale locale) throws NotAuthorizedException {
		Article ret = findOneByQuery(Query.n().eq(Article.FN_KEY, key).eq(Article.FN_LANG, locale.getLanguage()));
		if (ret == null && !fallbackToLocale.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
			ret = findOneByQuery(Query.n().eq(Article.FN_KEY, key).eq(Article.FN_LANG, fallbackToLocale.getLanguage()));
		}
		return ret;
	}

	@Override
	public Map<Locale, Article> findArticleLocalizations(String articleKey) {
		try {
			PaginatedList<Article> articleOptions = query(PagerParams.ALL, Query.n().eq(Article.FN_KEY, articleKey));
			Map<Locale, Article> ret = new HashMap<Locale, Article>(articleOptions.getItems().size());
			for (Article a : articleOptions.getItems()) {
				ret.put(new Locale(a.getLang()), a);
			}
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to find all article localizations", t);
		}
	}

	@Override
	public List<Article> findByGroup(String group, Locale locale) throws NotAuthorizedException {
		Preconditions.checkArgument(StringUtils.hasText(group));
		Preconditions.checkArgument(locale != null);

		return query(PagerParams.ALL, Query.n().eq(Article.FN_GROUP, group).eq(Article.FN_LANG, locale.getLanguage()))
				.getItems();
	}

	@Override
	public PaginatedList<Article> findArticles(PagerParams pagerParams, Locale locale) throws NotAuthorizedException {
		return query(pagerParams, Query.n().eq(Article.FN_LANG, locale.getLanguage()));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void addArticleAttachment(Attachment attachment) throws FieldValidationException, NotAuthorizedException {
		attachmentService.create(attachment);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void removeArticleAttachment(long attachmentId) throws NotAuthorizedException, EntityNotFoundException {
		attachmentService.deleteById(attachmentId);
	}

	@Override
	public Attachment[] findArticleAttachments(long articleId) throws NotAuthorizedException {
		PaginatedList<Attachment> results = attachmentService.query(PagerParams.ALL,
				Query.n().eq(Attachment.FN_ARTICLE_ID, articleId), OrderBy.Asc(Attachment.FN_NAME));
		return results.getItems().toArray(attachmentArrayType);
	}

	@Override
	public InputStream getAttachmnetContent(long attachmentId) throws NotAuthorizedException {
		return attachmentService.getContentInputStream(attachmentId);
	}

	public AttachmentService getAttachmentService() {
		return attachmentService;
	}

	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	public Locale getFallbackToLocale() {
		return fallbackToLocale;
	}

	public void setFallbackToLocale(Locale fallbackToLocale) {
		this.fallbackToLocale = fallbackToLocale;
	}

}
