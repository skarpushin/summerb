/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.minicms.api.dto.Article;
import org.summerb.validation.FieldValidationException;
import org.summerb.validation.ValidationContext;

public class ArticleValidationStrategyImpl implements EasyCrudValidationStrategy<Article> {
	@Override
	public void validateForCreate(Article dto) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		if (ctx.validateNotEmpty(dto.getArticleKey(), Article.FN_KEY)) {
			ctx.validateDataLengthLessOrEqual(dto.getArticleKey(), Article.FN_KEY_SIZE, Article.FN_KEY);
		}
		if (StringUtils.hasText(dto.getLang())) {
			ctx.validateDataLengthLessOrEqual(dto.getLang(), Article.FN_LANG_SIZE, Article.FN_LANG);
		}
		if (ctx.validateNotEmpty(dto.getTitle(), Article.FN_TITLE)) {
			ctx.validateDataLengthLessOrEqual(dto.getTitle(), Article.FN_TITLE_SIZE, Article.FN_TITLE);
		}
		if (StringUtils.hasText(dto.getAnnotation())) {
			ctx.validateDataLengthLessOrEqual(dto.getAnnotation(), Article.FN_ANNOTATION_SIZE, Article.FN_ANNOTATION);
		}
		if (ctx.validateNotEmpty(dto.getContent(), Article.FN_CONTENT)) {
			ctx.validateDataLengthLessOrEqual(dto.getContent(), Article.FN_CONTENT_SIZE, Article.FN_CONTENT);
		}
		if (StringUtils.hasText(dto.getArticleGroup())) {
			ctx.validateDataLengthLessOrEqual(dto.getArticleGroup(), Article.FN_GROUP_SIZE, Article.FN_GROUP);
		}

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}

	@Override
	public void validateForUpdate(Article existingVersion, Article newVersion) throws FieldValidationException {
		validateForCreate(newVersion);
	}
}
