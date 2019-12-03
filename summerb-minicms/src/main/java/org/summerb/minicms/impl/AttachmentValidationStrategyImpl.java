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
package org.summerb.minicms.impl;

import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.validation.FieldValidationException;
import org.summerb.validation.ValidationContext;

public class AttachmentValidationStrategyImpl implements EasyCrudValidationStrategy<Attachment> {
	@Override
	public void validateForUpdate(Attachment existingVersion, Attachment newVersion) throws FieldValidationException {
		throw new RuntimeException("Update operation is not supposed for attachments");
	}

	@Override
	public void validateForCreate(Attachment dto) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		if (ctx.validateNotEmpty(dto.getName(), Attachment.FN_NAME)) {
			ctx.validateDataLengthLessOrEqual(dto.getName(), Attachment.FN_NAME_MAXSIZE, Attachment.FN_NAME);
		}
		ctx.validateNotEmpty(dto.getArticleId(), Attachment.FN_ARTICLE_ID);
		ctx.validateNotEmpty(dto.getSize(), Attachment.FN_SIZE);
		ctx.validateNotNull(dto.getContents(), Attachment.FN_CONTENTS);

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}
}
