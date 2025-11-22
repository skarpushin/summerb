package org.summerb.easycrud.sql_builder;

import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.sql_builder.impl.ParamIdxIncrementer;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.utils.easycrud.api.dto.PagerParams;

/**
 * This is an encapsulation of reusable methods for building joined selects SQL components/clauses
 */
public interface SqlBuilder {
  QueryData findById(String tableName, Object id);

  QueryData deleteById(String tableName, Object id);

  QueryData deleteByIdOptimistic(String tableName, Object id, long modifiedAt);

  QueryData selectSingleRow(String tableName, Query<?, ?> query);

  QueryData deleteByQuery(String tableName, Query<?, ?> query);

  FromAndWhere fromAndWhere(String tableName, Query<?, ?> optionalQuery);

  QueryData countForSimpleSelect(FromAndWhere fromAndWhere);

  QueryData select(
      Class<?> rowClass,
      FromAndWhere fromAndWhere,
      Query<?, ?> optionalQuery,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      boolean countQueryWillFollow);

  QueryData queryForCountAfterPagedSelect(FromAndWhere fromAndWhere);

  void appendFromClause(
      JoinQuery<?, ?> joinQuery,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer);

  /**
   * @return true if added something
   */
  boolean appendFieldConditionsToWhereClause(
      List<Query<?, ?>> queries,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer);

  void appendOrderBy(OrderBy[] orderBy, JoinQuery<?, ?> joinQuery, StringBuilder sql);

  QueryData countForJoinedQuery(FromAndWhere fromAndWhere, JoinQuery<?, ?> joinQuery);

  FromAndWhere fromAndWhere(JoinQuery<?, ?> joinQuery);

  QueryData joinedSelect(
      JoinQuery<?, ?> joinQuery,
      List<Query<?, ?>> queries,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      FromAndWhere fromAndWhere);
}
