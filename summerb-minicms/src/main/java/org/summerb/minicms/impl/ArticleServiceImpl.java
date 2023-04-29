/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.minicms.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.minicms.api.ArticleDao;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.security.api.exceptions.NotAuthorizedException;

import com.google.common.base.Preconditions;

public class ArticleServiceImpl extends EasyCrudServicePluggableImpl<Long, Article, ArticleDao>
		implements ArticleService {

	private Locale fallbackToLocale = new Locale("en");

	public ArticleServiceImpl() {
		setRowClass(Article.class);
		setRowMessageCode("term.articles.article");

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
			PaginatedList<Article> articleOptions = find(PagerParams.ALL, Query.n().eq(Article.FN_KEY, articleKey));
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

		return find(PagerParams.ALL, Query.n().eq(Article.FN_GROUP, group).eq(Article.FN_LANG, locale.getLanguage()))
				.getItems();
	}

	@Override
	public PaginatedList<Article> findArticles(PagerParams pagerParams, Locale locale) throws NotAuthorizedException {
		return find(pagerParams, Query.n().eq(Article.FN_LANG, locale.getLanguage()));
	}

	public Locale getFallbackToLocale() {
		return fallbackToLocale;
	}

	public void setFallbackToLocale(Locale fallbackToLocale) {
		this.fallbackToLocale = fallbackToLocale;
	}

}
