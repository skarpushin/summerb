package org.summerb.microservices.articles.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.microservices.articles.api.dto.Article;

public interface ArticleService extends EasyCrudService<Long, Article> {

	Map<Locale, Article> findArticleLocalizations(String articleKey);

	List<Article> findByGroup(String group, Locale locale) throws NotAuthorizedException;

	PaginatedList<Article> findArticles(PagerParams pagerParams, Locale locale) throws NotAuthorizedException;

	Article findArticleByKeyAndLocale(String key, Locale locale) throws NotAuthorizedException;

	// NOTE: I'm not sure what I was thinking when I violated ISP here by adding
	// article attachment methods. There is a separate interface for that
	// AttachmentService, just use it

}
