/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.minicms.api.dto.consuming;

import java.util.List;

import org.summerb.minicms.api.dto.Article;

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
