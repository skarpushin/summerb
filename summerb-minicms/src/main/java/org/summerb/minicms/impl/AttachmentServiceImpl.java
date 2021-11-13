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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.minicms.api.AttachmentDao;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

public class AttachmentServiceImpl extends EasyCrudServicePluggableImpl<Long, Attachment, AttachmentDao>
		implements AttachmentService {
	private static Attachment[] attachmentArrayType = new Attachment[0];

	public AttachmentServiceImpl() {
		setDtoClass(Attachment.class);
		setEntityTypeMessageCode("term.articles.attachment");

		// Legacy:
		setWireTap(new EasyCrudWireTapValidationImpl<>(new AttachmentValidationStrategyImpl()));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Attachment create(Attachment dto) throws FieldValidationException, NotAuthorizedException {
		try {
			return super.create(dto);
		} finally {
			if (dto != null && dto.getContents() != null) {
				try {
					dto.getContents().close();
				} catch (IOException e) {
					// don't care
				}
			}
		}
	}

	@Override
	public InputStream getContentInputStream(long id) throws NotAuthorizedException {
		try {
			return dao.getContentInputStream(id);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public Attachment[] findArticleAttachments(long articleId) throws NotAuthorizedException {
		PaginatedList<Attachment> results = query(PagerParams.ALL, Query.n().eq(Attachment.FN_ARTICLE_ID, articleId),
				OrderBy.Asc(Attachment.FN_NAME));
		return results.getItems().toArray(attachmentArrayType);
	}

}
