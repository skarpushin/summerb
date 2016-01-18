package org.summerb.microservices.articles.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.summerb.microservices.articles.api.ArticleAbsoluteUrlBuilder;
import org.summerb.microservices.articles.api.ArticleRenderer;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.consuming.RenderedArticle;
import org.summerb.utils.stringtemplate.api.StringTemplate;
import org.summerb.utils.stringtemplate.api.StringTemplateCompiler;

import com.google.common.base.Preconditions;

public class ArticleRendererImpl implements ArticleRenderer {
	private ArticleService articleService;
	private ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder;
	private StringTemplateCompiler stringTemplateCompiler;

	@Override
	public RenderedArticle renderArticle(String key, Locale locale) {
		Preconditions.checkArgument(StringUtils.hasText(key));
		Preconditions.checkArgument(locale != null);

		try {
			Article article = articleService.findArticleByKeyAndLocale(key, locale);
			if (article == null) {
				throw new RuntimeException("Article not found: " + key + ", locale: " + locale);
			}

			RenderedArticle renderedArticle = buildRenderedArticleTemplate(article);
			ArticleRenderingContext articleRenderingContext = new ArticleRenderingContext(locale, renderedArticle,
					articleService, articleAbsoluteUrlBuilder);

			StringTemplate annotationTemplate = stringTemplateCompiler.compile(article.getAnnotation());
			renderedArticle.setAnnotation(annotationTemplate.applyTo(articleRenderingContext));
			StringTemplate contentTemplate = stringTemplateCompiler.compile(article.getContent());
			renderedArticle.setContent(contentTemplate.applyTo(articleRenderingContext));

			return renderedArticle;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to render article", t);
		}
	}

	private RenderedArticle buildRenderedArticleTemplate(Article article) {
		RenderedArticle renderedArticle = new RenderedArticle();
		renderedArticle.setId(article.getId());
		renderedArticle.setArticleKey(article.getArticleKey());
		renderedArticle.setLang(article.getLang());
		renderedArticle.setCreatedAt(article.getCreatedAt());
		renderedArticle.setCreatedBy(article.getCreatedBy());
		renderedArticle.setModifiedAt(article.getModifiedAt());
		renderedArticle.setModifiedBy(article.getModifiedBy());
		renderedArticle.setTitle(article.getTitle());
		renderedArticle.setArticleGroup(article.getArticleGroup());
		return renderedArticle;
	}

	public ArticleService getArticleService() {
		return articleService;
	}

	@Autowired
	public void setArticleService(ArticleService articleService) {
		this.articleService = articleService;
	}

	public ArticleAbsoluteUrlBuilder getArticleAbsoluteUrlBuilder() {
		return articleAbsoluteUrlBuilder;
	}

	@Autowired
	public void setArticleAbsoluteUrlBuilder(ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder) {
		this.articleAbsoluteUrlBuilder = articleAbsoluteUrlBuilder;
	}

	public StringTemplateCompiler getStringTemplateCompiler() {
		return stringTemplateCompiler;
	}

	@Autowired
	public void setStringTemplateCompiler(StringTemplateCompiler stringTemplateCompiler) {
		this.stringTemplateCompiler = stringTemplateCompiler;
	}

}
