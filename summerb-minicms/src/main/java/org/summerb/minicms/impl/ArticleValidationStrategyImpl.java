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

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.minicms.api.dto.Article;
import org.summerb.validation.ValidationContextFactory;
import org.summerb.validation.ValidationException;

public class ArticleValidationStrategyImpl implements EasyCrudValidationStrategy<Article> {
  @Autowired private ValidationContextFactory validationContextFactory;

  @Override
  public void validateForCreate(Article dto) throws ValidationException {
    var ctx = validationContextFactory.buildFor(dto);

    if (ctx.hasText(Article::getArticleKey)) {
      ctx.lengthLe(Article::getArticleKey, Article.FN_KEY_SIZE);
    }

    if (ctx.hasText(Article::getTitle)) {
      ctx.lengthLe(Article::getTitle, Article.FN_TITLE_SIZE);
    }

    if (ctx.hasText(Article::getContent)) {
      ctx.lengthLe(Article::getContent, Article.FN_CONTENT_SIZE);
    }

    ctx.lengthLe(Article::getLang, Article.FN_LANG_SIZE);
    ctx.lengthLe(Article::getAnnotation, Article.FN_ANNOTATION_SIZE);
    ctx.lengthLe(Article::getArticleGroup, Article.FN_GROUP_SIZE);

    ctx.throwIfHasErrors();
  }

  @Override
  public void validateForUpdate(Article existingVersion, Article newVersion)
      throws ValidationException {
    validateForCreate(newVersion);
  }
}
