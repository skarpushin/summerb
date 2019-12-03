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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.minicms.api.ArticleDao;
import org.summerb.minicms.api.AttachmentDao;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.validation.FieldValidationException;

/**
 * This simple impl is used to enforce attachments deletion before article will
 * be deleted. Unfortunately there is not transaction management. If later logic
 * fails it might happen that attachment files are deleted, but rows in both
 * articles and attachments tables remain. (that will happen if use @see
 * AttachmentDaoExtFilesImpl)
 * 
 * @author sergeyk
 *
 * @deprecated use {@link ArticleAttachmentRemoverWireTap} instead
 *
 */
@Deprecated
public class ArticleDaoAttachmentRemoverWrapper implements ArticleDao {
	private EasyCrudDao<Long, Article> articleDao;
	private AttachmentDao attachmentDao;

	@Override
	public void create(Article dto) throws FieldValidationException {
		articleDao.create(dto);
	}

	@Override
	public Article findById(Long id) {
		return articleDao.findById(id);
	}

	@Override
	public Article findOneByQuery(Query query) {
		return articleDao.findOneByQuery(query);
	}

	@Override
	public int delete(Long id) {
		removeArticleAttachments(id);
		return articleDao.delete(id);
	}

	private void removeArticleAttachments(Long id) {
		attachmentDao.deleteByQuery(Query.n().eq(Attachment.FN_ARTICLE_ID, id));
	}

	@Override
	public int delete(Long id, long modifiedAt) {
		removeArticleAttachments(id);
		return articleDao.delete(id, modifiedAt);
	}

	@Override
	public int update(Article dto) throws FieldValidationException {
		return articleDao.update(dto);
	}

	@Override
	public int deleteByQuery(Query query) {
		PaginatedList<Article> articles = articleDao.query(PagerParams.ALL, query);
		for (Article a : articles.getItems()) {
			removeArticleAttachments(a.getId());
		}

		return articleDao.deleteByQuery(query);
	}

	@Override
	public PaginatedList<Article> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy) {
		return articleDao.query(pagerParams, optionalQuery, orderBy);
	}

	public EasyCrudDao<Long, Article> getArticleDao() {
		return articleDao;
	}

	@Required
	public void setArticleDao(EasyCrudDao<Long, Article> articleDao) {
		this.articleDao = articleDao;
	}

	public AttachmentDao getAttachmentDao() {
		return attachmentDao;
	}

	@Autowired
	public void setAttachmentDao(AttachmentDao attachmentDao) {
		this.attachmentDao = attachmentDao;
	}

}
