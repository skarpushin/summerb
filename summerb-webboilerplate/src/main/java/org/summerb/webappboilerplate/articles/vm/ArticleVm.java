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

import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.webappboilerplate.model.ListPm;

import com.google.gson.Gson;

public class ArticleVm {
  private static final Gson cachedGson = new Gson();

  private Article dto;
  private ListPm<Attachment> attachments;
  private String dataAsJson;

  public String getDataAsJson() {
    if (dataAsJson == null) {
      dataAsJson = cachedGson.toJson(dto);
    }
    return dataAsJson;
  }

  public Article getDto() {
    return dto;
  }

  public void setDto(Article dto) {
    this.dto = dto;
    this.dataAsJson = null;
  }

  public ListPm<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(ListPm<Attachment> attachments) {
    this.attachments = attachments;
  }
}
