package org.summerb.microservices.articles.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.microservices.articles.api.ArticleDao;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.dto.Article;

import com.google.common.base.Preconditions;

public class ArticleServiceImpl extends EasyCrudServicePluggableImpl<Long, Article, ArticleDao>
		implements ArticleService {

	private Locale fallbackToLocale = new Locale("en");

	public ArticleServiceImpl() {
		setDtoClass(Article.class);
		setEntityTypeMessageCode("term.articles.article");

		// Legacy compatibility:
		setWireTap(new EasyCrudWireTapValidationImpl<>(new ArticleValidationStrategyImpl()));
	}

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

	public Locale getFallbackToLocale() {
		return fallbackToLocale;
	}

	public void setFallbackToLocale(Locale fallbackToLocale) {
		this.fallbackToLocale = fallbackToLocale;
	}

}
