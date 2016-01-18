package org.summerb.microservices.articles.api.dto.consuming;

import java.util.List;

import org.summerb.microservices.articles.api.dto.Article;

public class RenderedArticle extends Article {
	private static final long serialVersionUID = -810923943100116709L;

	private List<Long> articleReferences;

	public List<Long> getArticleReferences() {
		return articleReferences;
	}

	public void setArticleReferences(List<Long> articleReferences) {
		this.articleReferences = articleReferences;
	}

}
