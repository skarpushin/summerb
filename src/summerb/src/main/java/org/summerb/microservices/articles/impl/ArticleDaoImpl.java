package org.summerb.microservices.articles.impl;

import org.summerb.approaches.jdbccrud.impl.mysql.EasyCrudDaoMySqlImpl;
import org.summerb.microservices.articles.api.ArticleDao;
import org.summerb.microservices.articles.api.dto.Article;

public class ArticleDaoImpl extends EasyCrudDaoMySqlImpl<Long, Article> implements ArticleDao {
	public ArticleDaoImpl() {
		setDtoClass(Article.class);
	}
}
