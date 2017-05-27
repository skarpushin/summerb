package org.summerb.microservices.articles.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.AttachmentDao;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

/**
 * This simple impl is used to enforce attachments delition before article will
 * be deleted. Unfortunately ther eis not transaction management. If later logic
 * failes it might happen that attachment files are deleted, but rows in bot
 * articles and attachments tables remain. (that will happen if use @see
 * AttachmentDaoExtFilesImpl)
 * 
 * @author sergeyk
 *
 */
public class ArticleDaoAttachmentRemoverWrapper implements EasyCrudDao<Long, Article> {
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
