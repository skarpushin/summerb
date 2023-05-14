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
package integr.org.summerb.minicms.impl;

import org.summerb.minicms.api.ArticleAbsoluteUrlBuilder;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;

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
