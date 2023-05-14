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
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.validation.ValidationContextFactory;
import org.summerb.validation.ValidationException;

public class AttachmentValidationStrategyImpl implements EasyCrudValidationStrategy<Attachment> {
  @Autowired private ValidationContextFactory validationContextFactory;

  @Override
  public void validateForUpdate(Attachment existingVersion, Attachment newVersion)
      throws ValidationException {
    throw new RuntimeException("Update operation is not supposed for attachments");
  }

  @Override
  public void validateForCreate(Attachment dto) throws ValidationException {
    var ctx = validationContextFactory.buildFor(dto);
    if (ctx.hasText(Attachment::getName)) {
      ctx.lengthLe(Attachment::getName, Attachment.FN_NAME_MAXSIZE);
    }

    ctx.greater(Attachment::getArticleId, 0L);
    ctx.greater(Attachment::getSize, 0L);
    ctx.notNull(Attachment::getContents);

    ctx.throwIfHasErrors();
  }
}
