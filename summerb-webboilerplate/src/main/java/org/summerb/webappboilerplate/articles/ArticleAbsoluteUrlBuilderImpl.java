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
package org.summerb.webappboilerplate.articles;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.minicms.api.ArticleAbsoluteUrlBuilder;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.webappboilerplate.utils.AbsoluteUrlBuilder;

public class ArticleAbsoluteUrlBuilderImpl implements ArticleAbsoluteUrlBuilder {
  public static final String DEFAULT_PATH_ARTICLES_ATTACHMENTS = "/articles-attachments";
  public static final String DEFAULT_PATH_ARTICLES = "/articles";

  private String articlesBasePath = DEFAULT_PATH_ARTICLES;
  private String attachmentsBasePath = DEFAULT_PATH_ARTICLES_ATTACHMENTS;

  @Autowired protected AbsoluteUrlBuilder absoluteUrlBuilder;

  public ArticleAbsoluteUrlBuilderImpl() {}

  @Override
  public String buildUrlFroArticle(Article article) {
    return getBasePath() + articlesBasePath + "/" + article.getArticleKey();
  }

  @Override
  public String buildUrlFroArticleAttachment(Attachment attachment) {
    return getBasePath()
        + attachmentsBasePath
        + "/"
        + attachment.getId()
        + "/"
        + attachment.getName();
  }

  @Override
  public String buildUrlFroAppWebPage(String relativeUrl) {
    return getBasePath() + relativeUrl;
  }

  /** @return base url and context path (if any) */
  protected String getBasePath() {
    return absoluteUrlBuilder.buildExternalUrl(null);
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
