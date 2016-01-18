package integr.ru.skarpushin.services.articles.impl;

import org.summerb.microservices.articles.api.ArticleAbsoluteUrlBuilder;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

/**
 * This impl is used only for testing purposes
 * 
 * @author sergeyk
 * 
 */
public class UrlBuilderTestImpl implements ArticleAbsoluteUrlBuilder {
	@Override
	public String buildUrlFroArticleAttachment(Attachment attachment) {
		return "url-att:" + attachment.getName();
	}

	@Override
	public String buildUrlFroArticle(Article article) {
		return "url-article:" + article.getArticleKey();
	}

	@Override
	public String buildUrlFroAppWebPage(String relativeUrl) {
		return "relative:" + relativeUrl;
	}

}
