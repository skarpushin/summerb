package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByQueryResolver;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.Top;

public class SelectTemplate {
  protected static final PagerParams TOP_ONE = new Top(1);
  protected static final PagerParams TOP_TWO = new Top(2);

  protected JoinQuery<?, ?> joinQuery;
  protected SqlBuilder sqlBuilder;
  protected QuerySpecificsResolver querySpecificsResolver;
  protected NamedParameterJdbcTemplateEx jdbc;

  public SelectTemplate(
      NamedParameterJdbcTemplateEx jdbc,
      JoinQuery<?, ?> joinQuery,
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder) {
    Preconditions.checkNotNull(joinQuery, "joinQuery is required");
    Preconditions.checkNotNull(jdbc, "jdbc is required");
    Preconditions.checkNotNull(sqlBuilder, "sqlBuilder is required");
    Preconditions.checkNotNull(querySpecificsResolver, "querySpecificsResolver is required");

    this.jdbc = jdbc;
    this.joinQuery = joinQuery;
    this.querySpecificsResolver = querySpecificsResolver;
    this.sqlBuilder = sqlBuilder;
  }

  protected void assertOrderByHasReferencesToRegisteredQueries(OrderBy[] orderBy) {
    if (orderBy == null) {
      return;
    }

    for (OrderBy order : orderBy) {
      Preconditions.checkState(
          joinQuery.getQueries().contains(OrderByQueryResolver.get(order)),
          "OrderBy for field %s does not contain reference to a query that is mentioned in current JoinQuery. "
              + "When ordering rows retrieved using JoinQuery, make sure you instantiate them via JoinQuery or via participating Query");
    }
  }
}
