package org.summerb.microservices.articles.impl;

import org.summerb.approaches.jdbccrud.impl.EasyCrudDaoMySqlImpl;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.ArticleDao;

public class ArticleDaoImpl extends EasyCrudDaoMySqlImpl<Long, Article> implements ArticleDao {
	public ArticleDaoImpl() {
		setDtoClass(Article.class);
	}
}
