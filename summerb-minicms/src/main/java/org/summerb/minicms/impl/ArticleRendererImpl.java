/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.summerb.minicms.api.ArticleAbsoluteUrlBuilder;
import org.summerb.minicms.api.ArticleRenderer;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.consuming.RenderedArticle;
import org.summerb.stringtemplate.api.StringTemplate;
import org.summerb.stringtemplate.api.StringTemplateFactory;

import com.google.common.base.Preconditions;

public class ArticleRendererImpl implements ArticleRenderer {
  private ArticleService articleService;
  private AttachmentService attachmentService;
  private ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder;
  private StringTemplateFactory stringTemplateFactory;

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
      ArticleRenderingContext articleRenderingContext =
          new ArticleRenderingContext(
              locale,
              renderedArticle,
              articleService,
              attachmentService,
              articleAbsoluteUrlBuilder);

      StringTemplate annotationTemplate = stringTemplateFactory.build(article.getAnnotation());
      renderedArticle.setAnnotation(annotationTemplate.applyTo(articleRenderingContext));
      StringTemplate contentTemplate = stringTemplateFactory.build(article.getContent());
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

  public StringTemplateFactory getStringTemplateCompiler() {
    return stringTemplateFactory;
  }

  @Autowired
  public void setStringTemplateCompiler(StringTemplateFactory stringTemplateFactory) {
    this.stringTemplateFactory = stringTemplateFactory;
  }

  public AttachmentService getAttachmentService() {
    return attachmentService;
  }

  @Autowired
  public void setAttachmentService(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }
}
