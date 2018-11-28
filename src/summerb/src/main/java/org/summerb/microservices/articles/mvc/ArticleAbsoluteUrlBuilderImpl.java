package org.summerb.microservices.articles.mvc;

import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.microservices.articles.api.ArticleAbsoluteUrlBuilder;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

public class ArticleAbsoluteUrlBuilderImpl implements ArticleAbsoluteUrlBuilder {
	public static final String DEFAULT_PATH_ARTICLES_ATTACHMENTS = "/articles-attachments";
	public static final String DEFAULT_PATH_ARTICLES = "/articles";

	private String articlesBasePath = DEFAULT_PATH_ARTICLES;
	private String attachmentsBasePath = DEFAULT_PATH_ARTICLES_ATTACHMENTS;

	@Override
	public String buildUrlFroArticle(Article article) {
		return getBasePath() + articlesBasePath + "/" + article.getArticleKey();
	}

	@Override
	public String buildUrlFroArticleAttachment(Attachment attachment) {
		return getBasePath() + attachmentsBasePath + "/" + attachment.getId() + "/" + attachment.getName();
	}

	@Override
	public String buildUrlFroAppWebPage(String relativeUrl) {
		return getBasePath() + relativeUrl;
	}

	/**
	 * @return base url and context path (if any)
	 */
	protected String getBasePath() {
		return CurrentRequestUtils.get().getContextPath();
	}

	public String getArticlesBasePath() {
		return articlesBasePath;
	}

	public void setArticlesBasePath(String articlesBasePath) {
		this.articlesBasePath = articlesBasePath;
	}

	public String getAttachmentsBasePath() {
		return attachmentsBasePath;
	}

	public void setAttachmentsBasePath(String attachmentsBasePath) {
		this.attachmentsBasePath = attachmentsBasePath;
	}

}
