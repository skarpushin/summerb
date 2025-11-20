package org.summerb.easycrud.sql_builder.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.join_query.ConditionsLocation;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.impl.JoinQueryElement;
import org.summerb.easycrud.join_query.model.JoinType;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.row.HasTimestamps;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.easycrud.sql_builder.model.SelectedColumn;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;
import org.summerb.utils.easycrud.api.dto.PagerParams;

public class SqlBuilderCommonImpl implements SqlBuilder {
  protected final Logger log = LoggerFactory.getLogger(getClass());
  protected QuerySpecificsResolver querySpecificsResolver;
  protected FieldsEnlister fieldsEnlister;
  protected QueryToSql queryToSql;
  protected OrderByToSql orderByToSql;
  protected String sqlPartPaginator = "\nLIMIT :max OFFSET :offset";

  public SqlBuilderCommonImpl(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    Preconditions.checkNotNull(querySpecificsResolver, "querySpecificsResolver is required");
    Preconditions.checkNotNull(fieldsEnlister, "fieldsEnlister is required");
    Preconditions.checkNotNull(queryToSql, "queryToSql is required");
    Preconditions.checkNotNull(orderByToSql, "orderByToSql is required");

    this.querySpecificsResolver = querySpecificsResolver;
    this.fieldsEnlister = fieldsEnlister;
    this.queryToSql = queryToSql;
    this.orderByToSql = orderByToSql;
  }

  @Override
  public QueryData findById(String tableName, Object id) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");
    Preconditions.checkArgument(id != null, "id is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);
    String sql = "SELECT * FROM " + tableName + " WHERE id = :id";
    return new QueryData(sql, params);
  }

  @Override
  public QueryData deleteById(String tableName, Object id) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");
    Preconditions.checkArgument(id != null, "id is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);
    String sql = "DELETE FROM " + tableName + " WHERE id = :id";
    return new QueryData(sql, params);
  }

  @Override
  public QueryData deleteByIdOptimistic(String tableName, Object id, long modifiedAt) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");
    Preconditions.checkArgument(id != null, "id is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);
    params.addValue(HasTimestamps.FN_MODIFIED_AT, modifiedAt);
    String sql = "DELETE FROM " + tableName + " WHERE id = :id AND modified_at = :modifiedAt";
    return new QueryData(sql, params);
  }

  @Override
  public QueryData selectSingleRow(String tableName, Query<?, ?> query) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");
    Preconditions.checkArgument(
        query != null && !CollectionUtils.isEmpty(query.getConditions()), "query is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    String whereClause = queryToSql.buildFilter(query, params);
    String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause;
    return new QueryData(sql, params);
  }

  @Override
  public QueryData deleteByQuery(String tableName, Query<?, ?> query) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");
    Preconditions.checkArgument(
        query != null && !CollectionUtils.isEmpty(query.getConditions()), "query is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    String sql = "DELETE FROM " + tableName + " WHERE " + queryToSql.buildFilter(query, params);
    return new QueryData(sql, params);
  }

  @Override
  public FromAndWhere fromAndWhere(String tableName, Query<?, ?> optionalQuery) {
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is required");

    MapSqlParameterSource params = new MapSqlParameterSource();
    String whereClause =
        optionalQuery == null || optionalQuery.isEmpty()
            ? ""
            : "\nWHERE " + queryToSql.buildFilter(optionalQuery, params);
    String sql = "\nFROM " + tableName + whereClause;
    return new FromAndWhere(sql, params);
  }

  @Override
  public QueryData countForSimpleSelect(FromAndWhere fromAndWhere) {
    String sql = "SELECT COUNT(id)" + fromAndWhere.getSql();
    return new QueryData(sql, fromAndWhere.getParams());
  }

  @Override
  public QueryData selectMultipleRows(
      Class<?> rowClass,
      FromAndWhere fromAndWhere,
      Query<?, ?> optionalQuery,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      boolean countQueryWillFollow) {
    Preconditions.checkNotNull(fromAndWhere, "fromAndWhere is required");

    fromAndWhere.getParams().addValue(PagerParams.FIELD_OFFSET, pagerParams.getOffset());
    fromAndWhere.getParams().addValue(PagerParams.FIELD_MAX, pagerParams.getMax());

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT");
    List<ColumnsSelection> columnSelections = new LinkedList<>();
    appendColumnsSelection(
        rowClass, null, optionalQuery, orderBy, true, false, false, sql, columnSelections);
    appendAdditionalColumnsSelectionIfNeeded(
        rowClass, null, optionalQuery, orderBy, true, false, false, sql, columnSelections);

    sql.append(fromAndWhere.getSql());
    sql.append(orderByToSql.buildOrderBySubclause(orderBy));

    if (!PagerParams.ALL.equals(pagerParams)) {
      sql.append(sqlPartPaginator);
    }

    QueryData ret = new QueryData(sql.toString(), fromAndWhere.getParams());
    ret.setSelectedColumns(columnSelections);
    return ret;
  }

  /** Subclass can override this to add some other columns after all selection columns are added */
  protected <TId, TRow extends HasId<TId>> void appendAdditionalColumnsSelectionIfNeeded(
      Class<?> rowClass,
      JoinQuery<?, ?> optionalJoinQuery,
      Query<TId, TRow> optionalQuery,
      OrderBy[] orderBy,
      boolean wildcardAllowed,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder outSql,
      List<ColumnsSelection> outColumns) {
    // no impl, just an extension point
  }

  @Override
  public QueryData queryForCountAfterPagedSelect(FromAndWhere fromAndWhere) {
    Preconditions.checkNotNull(fromAndWhere, "fromAndWhere is required");

    String sql = "SELECT count(*) " + fromAndWhere.getSql();
    return new QueryData(sql, fromAndWhere.getParams());
  }

  @Override
  public QueryData joinedSingleTableSingleRow(JoinQuery<?, ?> joinQuery, Query<?, ?> query) {
    StringBuilder sql = new StringBuilder();
    MapSqlParameterSource params = new MapSqlParameterSource();

    sql.append("SELECT");
    List<ColumnsSelection> columnSelections = new LinkedList<>();
    appendColumnsSelection(
        querySpecificsResolver.getRowClass(query),
        joinQuery,
        query,
        null,
        true,
        true,
        false,
        sql,
        columnSelections);
    appendAdditionalColumnsSelectionIfNeeded(
        querySpecificsResolver.getRowClass(query),
        joinQuery,
        query,
        null,
        true,
        true,
        false,
        sql,
        columnSelections);
    buildFromAndWhere(joinQuery, sql, params);

    QueryData ret = new QueryData(sql.toString(), params);
    ret.setSelectedColumns(columnSelections);
    logQuery(ret);
    return ret;
  }

  protected void logQuery(QueryData ret) {
    if (log.isDebugEnabled()) {
      log.debug("Query: {}", ret.getSql());
      log.debug("Parameters: {}", ret.getParams());
    }
  }

  protected void buildFromAndWhere(
      JoinQuery<?, ?> joinQuery, StringBuilder sql, MapSqlParameterSource params) {
    ParamIdxIncrementer paramIdxIncrementer = new ParamIdxIncrementer();

    sql.append("\nFROM");
    appendFromClause(joinQuery, sql, params, paramIdxIncrementer);

    List<Query<?, ?>> conditions =
        joinQuery.getQueries().stream()
            .filter(
                x ->
                    !x.getConditions().isEmpty()
                        && joinQuery.getConditionsLocationForQuery(x) == ConditionsLocation.WHERE)
            .toList();

    if (!conditions.isEmpty() || !joinQuery.getNotExists().isEmpty()) {
      sql.append("\nWHERE");
      boolean added =
          appendFieldConditionsToWhereClause(conditions, sql, params, paramIdxIncrementer);
      appendNotExistsToWhereClause(
          joinQuery.getNotExists(), added, sql, params, paramIdxIncrementer);
    }
  }

  @Override
  public QueryData countForJoinedQuery(FromAndWhere fromAndWhere, JoinQuery<?, ?> joinQuery) {
    String sql = "SELECT COUNT(*)" + fromAndWhere.getSql();
    QueryData ret = new QueryData(sql, fromAndWhere.getParams());
    logQuery(ret);
    return ret;
  }

  @Override
  public FromAndWhere fromAndWhere(JoinQuery<?, ?> joinQuery) {
    StringBuilder sql = new StringBuilder();
    MapSqlParameterSource params = new MapSqlParameterSource();
    buildFromAndWhere(joinQuery, sql, params);
    return new FromAndWhere(sql.toString(), params);
  }

  @Override
  public QueryData joinedSingleTableMultipleRows(
      JoinQuery<?, ?> joinQuery,
      Query<?, ?> query,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      FromAndWhere fromAndWhere) {
    return joinedMultipleTablesMultipleRows(
        joinQuery, List.of(query), pagerParams, orderBy, fromAndWhere);
  }

  @Override
  public QueryData joinedMultipleTablesMultipleRows(
      JoinQuery<?, ?> joinQuery,
      List<Query<?, ?>> queries,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      FromAndWhere fromAndWhere) {
    StringBuilder sql = new StringBuilder();

    sql.append("SELECT");

    List<ColumnsSelection> columnSelections = new LinkedList<>();
    int queriesCount = queries.size();
    for (int i = 0; i < queriesCount; i++) {
      Query<?, ?> query = queries.get(i);
      if (i > 0) {
        sql.append(",");
      }
      appendColumnsSelection(
          querySpecificsResolver.getRowClass(query),
          joinQuery,
          query,
          orderBy,
          queriesCount == 1,
          true,
          queriesCount > 1,
          sql,
          columnSelections);
    }

    appendAdditionalColumnsSelectionIfNeeded(
        null,
        joinQuery,
        null,
        orderBy,
        queriesCount == 1,
        true,
        queriesCount > 1,
        sql,
        columnSelections);

    sql.append(fromAndWhere.getSql());

    if (orderBy != null && orderBy.length > 0) {
      sql.append("\nORDER BY ");
      appendOrderBy(orderBy, joinQuery, sql);
    }

    if (!PagerParams.ALL.equals(pagerParams)) {
      fromAndWhere.getParams().addValue(PagerParams.FIELD_OFFSET, pagerParams.getOffset());
      fromAndWhere.getParams().addValue(PagerParams.FIELD_MAX, pagerParams.getMax());
      sql.append(sqlPartPaginator);
    }

    QueryData ret = new QueryData(sql.toString(), fromAndWhere.getParams());
    ret.setSelectedColumns(columnSelections);
    logQuery(ret);
    return ret;
  }

  @Override
  public void appendFromClause(
      JoinQuery<?, ?> joinQuery,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer) {
    Set<Query<?, ?>> seen = new HashSet<>();
    Query<?, ?> primaryQuery = joinQuery.getPrimaryQuery();
    String primaryTableName = querySpecificsResolver.getTableName(primaryQuery);

    sql.append("\n\t").append(primaryTableName);
    if (!primaryTableName.equals(primaryQuery.getAlias())) {
      sql.append(" AS ").append(primaryQuery.getAlias());
    }

    seen.add(primaryQuery);

    for (JoinQueryElement join : joinQuery.getJoins()) {
      sql.append("\n\t").append(joinTypeToSql(join.getJoinType())).append(" ");

      if (!seen.contains(join.getReferer())) {
        appendJoinFromReferer(joinQuery, sql, params, paramIdxIncrementer, join, seen);
      } else if (!seen.contains(join.getReferred())) {
        appendJoinFromReferred(joinQuery, sql, params, paramIdxIncrementer, join, seen);
      } else {
        throw new IllegalStateException("Failed to build JOINs -- Query was already seen");
      }
    }
  }

  protected void appendJoinFromReferred(
      JoinQuery<?, ?> joinQuery,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer,
      JoinQueryElement join,
      Set<Query<?, ?>> seen) {

    seen.add(join.getReferred());

    String refererAlias = join.getReferer().getAlias();

    String referredTableName = querySpecificsResolver.getTableName(join.getReferred());
    String referredAlias = join.getReferred().getAlias();

    sql.append(referredTableName);
    if (!referredTableName.equals(referredAlias)) {
      sql.append(" AS ").append(referredAlias);
    }
    sql.append(" ON ")
        .append(refererAlias)
        .append(".")
        .append(QueryToSqlMySqlImpl.snakeCase(join.getOtherIdGetterFieldName()))
        .append(" = ")
        .append(referredAlias)
        .append(".id");

    if (!join.getReferred().getConditions().isEmpty()
        && joinQuery.getConditionsLocationForQuery(join.getReferred()) == ConditionsLocation.JOIN) {
      sql.append(" AND ");
      queryToSql.buildFilter(join.getReferred(), params, referredAlias, sql, paramIdxIncrementer);
    }
  }

  protected void appendJoinFromReferer(
      JoinQuery<?, ?> joinQuery,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer,
      JoinQueryElement join,
      Set<Query<?, ?>> seen) {

    seen.add(join.getReferer());

    String refererTableName = querySpecificsResolver.getTableName(join.getReferer());
    String refererAlias = join.getReferer().getAlias();

    String referredAlias = join.getReferred().getAlias();

    sql.append(refererTableName);
    if (!refererTableName.equals(refererAlias)) {
      sql.append(" AS ").append(refererAlias);
    }

    sql.append(" ON ")
        .append(refererAlias)
        .append(".")
        .append(QueryToSqlMySqlImpl.snakeCase(join.getOtherIdGetterFieldName()))
        .append(" = ")
        .append(referredAlias)
        .append(".id");

    if (!join.getReferer().getConditions().isEmpty()
        && joinQuery.getConditionsLocationForQuery(join.getReferer()) == ConditionsLocation.JOIN) {
      sql.append(" AND ");
      queryToSql.buildFilter(join.getReferer(), params, refererAlias, sql, paramIdxIncrementer);
    }
  }

  @Override
  public boolean appendFieldConditionsToWhereClause(
      List<Query<?, ?>> queries,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer) {
    boolean added = false;
    for (Query<?, ?> query : queries) {
      if (query.getConditions().isEmpty()) {
        continue;
      }
      if (added) {
        sql.append("\n\tAND ");
      } else {
        sql.append("\n\t");
      }
      queryToSql.buildFilter(query, params, query.getAlias(), sql, paramIdxIncrementer);
      added = true;
    }
    return added;
  }

  protected boolean appendNotExistsToWhereClause(
      List<JoinQueryElement> notExists,
      boolean added,
      StringBuilder sql,
      MapSqlParameterSource params,
      ParamIdxIncrementer paramIdxIncrementer) {

    for (JoinQueryElement join : notExists) {
      if (join.getReferer().isGuaranteedToYieldEmptyResultset()) {
        continue;
      }

      if (added) {
        sql.append("\n\tAND ");
      } else {
        sql.append("\n\t");
      }

      sql.append("NOT EXISTS (SELECT 1 FROM ");

      String refererTableName = querySpecificsResolver.getTableName(join.getReferer());
      String refererAlias = join.getReferer().getAlias();

      String referredAlias = join.getReferred().getAlias();

      sql.append(refererTableName);
      if (!refererTableName.equals(refererAlias)) {
        sql.append(" AS ").append(refererAlias);
      }
      sql.append(" WHERE ")
          .append(refererAlias)
          .append(".")
          .append(QueryToSqlMySqlImpl.snakeCase(join.getOtherIdGetterFieldName()))
          .append(" = ")
          .append(referredAlias)
          .append(".id");

      if (!join.getReferer().getConditions().isEmpty()) {
        sql.append(" AND ");
        queryToSql.buildFilter(
            join.getReferer(), params, join.getReferer().getAlias(), sql, paramIdxIncrementer);
      }
      sql.append(")");

      added = true;
    }

    return added;
  }

  protected String joinTypeToSql(JoinType joinType) {
    return switch (joinType) {
      case INNER -> "JOIN";
      case LEFT -> "LEFT JOIN";
      case NOT_EXISTS ->
          throw new IllegalArgumentException("NOT_EXISTS must not be used in this context");
    };
  }

  protected <TId, TRow extends HasId<TId>> void appendColumnsSelection(
      Class<?> rowClass,
      JoinQuery<?, ?> optionalJoinQuery,
      Query<TId, TRow> optionalQuery,
      OrderBy[] orderBy,
      boolean wildcardAllowed,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder outSql,
      List<ColumnsSelection> outColumns) {

    if (wildcardAllowed) {
      if (prefixColumnsWhenReferencing) {
        Preconditions.checkArgument(
            optionalQuery != null && optionalQuery.getAlias() != null, "query is missing alias");
        outSql.append(" ").append(optionalQuery.getAlias()).append(".*");
      } else {
        outSql.append(" *");
      }

      ColumnsSelection ret = new ColumnsSelection();
      ret.setQuery(optionalQuery);
      ret.setWildcardAdded(true);
      outColumns.add(ret);
      return;
    }

    List<String> fieldNames = fieldsEnlister.findInClass(rowClass);
    Preconditions.checkState(!CollectionUtils.isEmpty(fieldNames), "No fields found in class");

    outSql.append("\n\t");
    List<SelectedColumn> selected = new ArrayList<>(fieldNames.size());
    for (String fieldName : fieldNames) {
      if (!selected.isEmpty()) {
        outSql.append(", ");
      }

      selected.add(
          appendColumnSelection(
              optionalJoinQuery,
              optionalQuery,
              orderBy,
              fieldName,
              prefixColumnsWhenReferencing,
              selectAsPrefixedAliasedNames,
              outSql));
    }

    ColumnsSelection ret = new ColumnsSelection();
    ret.setQuery(optionalQuery);
    ret.setColumns(selected);
    outColumns.add(ret);
  }

  protected SelectedColumn appendColumnSelection(
      JoinQuery<?, ?> optionalJoinQuery,
      Query<?, ?> optionalQuery,
      OrderBy[] orderBy,
      String fieldName,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder sql) {

    if (prefixColumnsWhenReferencing) {
      sql.append(optionalQuery.getAlias()).append(".");
    }

    String columnName = QueryToSqlMySqlImpl.snakeCase(fieldName);
    sql.append(columnName);

    String columnLabel;
    if (selectAsPrefixedAliasedNames) {
      Preconditions.checkState(
          optionalQuery != null && optionalQuery.getAlias() != null,
          "prefixWithAlias == true, while query is either missing or missing alias");
      columnLabel = optionalQuery.getAlias() + "_" + columnName;
    } else {
      columnLabel = columnName;
    }

    if (selectAsPrefixedAliasedNames) {
      sql.append(" AS ").append(columnLabel);
    }
    return new SelectedColumn(fieldName, columnName, columnLabel);
  }

  @Override
  public void appendOrderBy(OrderBy[] orderBy, JoinQuery<?, ?> joinQuery, StringBuilder sql) {
    orderByToSql.appendOrderByElements(orderBy, joinQuery, sql);
  }
}
