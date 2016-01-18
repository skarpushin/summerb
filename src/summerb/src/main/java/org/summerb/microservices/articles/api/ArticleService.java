package org.summerb.microservices.articles.api;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

public interface ArticleService {
	Article create(Article dto) throws FieldValidationException, NotAuthorizedException;

	Article update(Article dto) throws FieldValidationException, NotAuthorizedException, EntityNotFoundException;

	Article findById(Long id) throws NotAuthorizedException;

	Map<Locale, Article> findArticleLocalizations(String articleKey);

	List<Article> findByGroup(String group, Locale locale) throws NotAuthorizedException;

	PaginatedList<Article> findArticles(PagerParams pagerParams, Locale locale) throws NotAuthorizedException;

	void deleteByIdOptimistic(Long id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException;

	Article findArticleByKeyAndLocale(String key, Locale locale) throws NotAuthorizedException;

	void addArticleAttachment(Attachment attachment) throws FieldValidationException, NotAuthorizedException;

	void removeArticleAttachment(long attachmentId) throws NotAuthorizedException, EntityNotFoundException;

	Attachment[] findArticleAttachments(long articleId) throws NotAuthorizedException;

	InputStream getAttachmnetContent(long attachmentId) throws NotAuthorizedException;

}
