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
package org.summerb.webappboilerplate.articles.vm;

import java.util.List;
import java.util.Map;

import org.summerb.utils.collection.DummyMapImpl;

public class ArticlesVm {
  private final List<ArticleVm> contents;

  public ArticlesVm(List<ArticleVm> contents) {
    this.contents = contents;
  }

  private final Map<String, ArticleVm> map =
      new DummyMapImpl<String, ArticleVm>() {
        @Override
        public ArticleVm get(Object key) {
          for (ArticleVm localization : contents) {
            if (localization.getDto().getLang().equals(key)) {
              return localization;
            }
          }
          throw new RuntimeException("Article localization for " + key + " not found");
        }
      };

  public Map<String, ArticleVm> getMap() {
    return map;
  }
}
