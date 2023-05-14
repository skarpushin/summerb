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
package org.summerb.minicms.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.minicms.api.dto.Article;
import org.summerb.security.api.exceptions.NotAuthorizedException;

public interface ArticleService extends EasyCrudService<Long, Article> {

  Map<Locale, Article> findArticleLocalizations(String articleKey);

  List<Article> findByGroup(String group, Locale locale) throws NotAuthorizedException;

  PaginatedList<Article> findArticles(PagerParams pagerParams, Locale locale)
      throws NotAuthorizedException;

  Article findArticleByKeyAndLocale(String key, Locale locale) throws NotAuthorizedException;

  // NOTE: I'm not sure what I was thinking when I violated ISP here by adding
  // article attachment methods. There is a separate interface for that
  // AttachmentService, just use it

}
