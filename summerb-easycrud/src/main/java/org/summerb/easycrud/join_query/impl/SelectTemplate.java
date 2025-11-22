package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.model.JoinType;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByQueryResolver;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.Top;

public class SelectTemplate {
  protected static final PagerParams TOP_ONE = new Top(1);
  protected static final PagerParams TOP_TWO = new Top(2);

  protected JoinQuery<?, ?> joinQuery;
  protected SqlBuilder sqlBuilder;
  protected QuerySpecificsResolver querySpecificsResolver;
  protected NamedParameterJdbcTemplateEx jdbc;
  protected FieldsEnlister fieldsEnlister;

  public SelectTemplate(
      NamedParameterJdbcTemplateEx jdbc,
      JoinQuery<?, ?> joinQuery,
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      FieldsEnlister fieldsEnlister) {
    Preconditions.checkArgument(joinQuery != null, "joinQuery is required");
    Preconditions.checkArgument(jdbc != null, "jdbc is required");
    Preconditions.checkArgument(sqlBuilder != null, "sqlBuilder is required");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver is required");
    Preconditions.checkArgument(fieldsEnlister != null, "fieldsEnlister is required");

    this.jdbc = jdbc;
    this.joinQuery = joinQuery;
    this.querySpecificsResolver = querySpecificsResolver;
    this.sqlBuilder = sqlBuilder;
    this.fieldsEnlister = fieldsEnlister;
  }

  /**
   * Make sure all given orderBy are referencing queries from our JoinQuery. And if that is not the
   * case, then we copy-paste them and attempt to mix and match based on aliases and field names. If
   * no alias is given by orderBy ({@link OrderBy#getFieldName()} does not contain a dot), then we
   * attempt to match with field in the {@link JoinQuery#getPrimaryQuery()}
   */
  protected OrderBy[] ensureOrderByReferenceRegisteredQueries(OrderBy[] orderBy) {
    if (orderBy == null) {
      return null;
    }

    // As soon as we bump into first inconsistency we populate new array.
    // Reason for new array is that it is possible that we're receiving some constant array -- so we
    // want to avoid any issues with modifying existing aray
    OrderBy[] newArr = null;

    for (int i = 0; i < orderBy.length; i++) {
      OrderBy order = orderBy[i];
      if (newArr != null) {
        orderBy[i] = order;
      }

      Query<?, ?> query = OrderByQueryResolver.get(order);

      // Order by contains link to our query -- great
      if (query != null && joinQuery.getJoinedQueries().contains(query)) {
        continue;
      }

      if (newArr == null) {
        newArr = new OrderBy[orderBy.length];
        if (i > 0) {
          System.arraycopy(orderBy, 0, newArr, 0, i);
        }
      }

      // Order by does not contain link to query or query is not ours.
      // Attempt to match field name with query
      newArr[i] = matchAndConvert(order);
    }

    return newArr != null ? newArr : orderBy;
  }

  protected OrderBy matchAndConvert(OrderBy order) {
    int indexOfDot = order.getFieldName().indexOf(".");
    String alias = indexOfDot > 0 ? order.getFieldName().substring(0, indexOfDot) : null;
    String fieldName =
        indexOfDot > 0 ? order.getFieldName().substring(indexOfDot + 1) : order.getFieldName();

    Query<?, ?> query = findQueryByAlias(alias, fieldName);
    if (query == null) {
      throw new IllegalArgumentException(
          "Could not distinctively match orderBy on field "
              + order.getFieldName()
              + " to this join query");
    }

    OrderBy newOrder = new OrderBy(fieldName, order.getDirection(), query);
    newOrder.setNullsLast(order.getNullsLast());
    newOrder.setCollate(order.getCollate());
    return newOrder;
  }

  protected Query<?, ?> findQueryByAlias(String alias, String fieldName) {
    if (alias == null) {
      if (isRowHasFieldWithGivenName(joinQuery.getPrimaryQuery(), fieldName)) {
        return joinQuery.getPrimaryQuery();
      }
    } else {
      Query<?, ?> query =
          joinQuery.getJoinedQueries().stream()
              .filter(x -> alias.equals(x.getAlias()))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalArgumentException("Can't find query with alias: " + alias));
      if (isRowHasFieldWithGivenName(query, fieldName)) {
        return query;
      }
    }
    return null;
  }

  protected boolean isRowHasFieldWithGivenName(Query<?, ?> query, String fieldName) {
    Class<?> rowClass = querySpecificsResolver.getRowClass(query);
    return fieldsEnlister.findInClass(rowClass).stream().anyMatch(x -> x.equals(fieldName));
  }

  protected boolean isGuaranteedToYieldEmptyResultset() {
    return joinQuery.getJoinedQueries().stream().anyMatch(Query::isGuaranteedToYieldEmptyResultset)
        || joinQuery.getExistenceConditions().stream()
            .anyMatch(
                x ->
                    x.getJoinType() == JoinType.EXISTS
                        && x.getReferer().isGuaranteedToYieldEmptyResultset());
  }

  protected ResultSetExtractorJoinedQueryImpl buildResultSetExtractor(
      List<Query<?, ?>> entitiesToSelect, QueryData queryData) {
    return new ResultSetExtractorJoinedQueryImpl(
        entitiesToSelect, queryData.getSelectedColumns(), querySpecificsResolver);
  }
}
