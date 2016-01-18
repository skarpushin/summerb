package org.summerb.microservices.articles.api;

import java.util.Locale;

import org.summerb.microservices.articles.api.dto.consuming.RenderedArticle;

public interface ArticleRenderer {
	RenderedArticle renderArticle(String key, Locale locale);
}
