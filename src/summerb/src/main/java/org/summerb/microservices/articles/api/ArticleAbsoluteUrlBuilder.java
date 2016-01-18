package org.summerb.microservices.articles.api;

import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

public interface ArticleAbsoluteUrlBuilder {
	String buildUrlFroArticleAttachment(Attachment attachment);

	String buildUrlFroArticle(Article attachment);

	String buildUrlFroAppWebPage(String relativeUrl);
}
