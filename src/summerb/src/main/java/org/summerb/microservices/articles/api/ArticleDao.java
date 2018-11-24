package org.summerb.microservices.articles.api;

import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.microservices.articles.api.dto.Article;

public interface ArticleDao extends EasyCrudDao<Long, Article> {

}
