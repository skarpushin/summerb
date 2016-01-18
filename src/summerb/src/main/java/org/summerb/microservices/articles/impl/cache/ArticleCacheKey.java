package org.summerb.microservices.articles.impl.cache;

import java.util.Locale;

import org.summerb.microservices.articles.api.dto.Article;

public class ArticleCacheKey {
	private String articleKey;
	private String lang;

	public ArticleCacheKey(String articleKey, Locale locale) {
		this.articleKey = articleKey;
		this.lang = locale.getLanguage();
	}

	public ArticleCacheKey(Article article) {
		this.articleKey = article.getArticleKey();
		this.lang = article.getLang();
	}

	public String getArticleKey() {
		return articleKey;
	}

	public void setArticleKey(String articleKey) {
		this.articleKey = articleKey;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleKey == null) ? 0 : articleKey.hashCode());
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleCacheKey other = (ArticleCacheKey) obj;
		if (articleKey == null) {
			if (other.articleKey != null)
				return false;
		} else if (!articleKey.equals(other.articleKey))
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		return true;
	}
}
