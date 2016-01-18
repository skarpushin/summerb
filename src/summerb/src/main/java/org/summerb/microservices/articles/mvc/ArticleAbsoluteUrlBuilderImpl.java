package org.summerb.microservices.articles.mvc;

import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.microservices.articles.api.ArticleAbsoluteUrlBuilder;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

public class ArticleAbsoluteUrlBuilderImpl implements ArticleAbsoluteUrlBuilder {

	@Override
	public String buildUrlFroArticle(Article article) {
		return CurrentRequestUtils.get().getContextPath() + "/articles/" + article.getArticleKey();
	}

	@Override
	public String buildUrlFroArticleAttachment(Attachment attachment) {
		return CurrentRequestUtils.get().getContextPath() + "/articles-attachments/" + attachment.getId() + "/"
				+ attachment.getName();
	}

	@Override
	public String buildUrlFroAppWebPage(String relativeUrl) {
		return CurrentRequestUtils.get().getContextPath() + relativeUrl;
	}

}
