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
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

/**
 * This WireTap is usefull when we either need to monitor cascade attachment deletions on event bus,
 * or we're using {@link AttachmentDaoExtFilesImpl} and we need to make sure there will be process
 * in place which removes actual files from disk (you supposed to register {@link
 * AttachmentFilesRemover} bean and you have pre-commit event bus and post-commit event bus.
 *
 * @see com.google.common.eventbus.AsyncEventBus
 * @see org.summerb.utils.tx.AfterCommitExecutorThreadLocalImpl
 * @author sergeyk
 */
public class ArticleAttachmentRemoverWireTap extends EasyCrudWireTapNoOpImpl<Long, Article> {
  @Autowired private AttachmentService attachmentService;

  @Override
  public boolean requiresOnDelete() throws ValidationException, NotAuthorizedException {
    return true;
  }

  @Override
  public boolean requiresFullDto() {
    return false;
  }

  @Override
  public void beforeDelete(Article dto) throws ValidationException, NotAuthorizedException {
    attachmentService.deleteByQuery(Query.n().eq(Attachment.FN_ARTICLE_ID, dto.getId()));
  }
}
