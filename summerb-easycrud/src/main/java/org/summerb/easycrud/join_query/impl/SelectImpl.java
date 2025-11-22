package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.Select;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.impl.ParamIdxIncrementer;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;

@SuppressWarnings("SqlSourceToSinkFlow")
public class SelectImpl<TId, TRow extends HasId<TId>> extends SelectTemplate
    implements Select<TId, TRow> {

  protected record PageLoadResults<TRow>(FromAndWhere fromAndWhere, List<TRow> list) {}

  protected Query<TId, TRow> entityToSelect;

  public SelectImpl(
      JoinQuery<?, ?> joinQuery,
      Query<TId, TRow> entityToSelect,
      NamedParameterJdbcTemplateEx jdbc,
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      FieldsEnlister fieldsEnlister) {
    super(jdbc, joinQuery, querySpecificsResolver, sqlBuilder, fieldsEnlister);

    Preconditions.checkArgument(entityToSelect != null, "entityToSelect is required");

    this.entityToSelect = entityToSelect;
  }

  @Override
  public TRow findOne() {
    PaginatedList<TRow> result = find(TOP_TWO);
    if (!result.getHasItems()) {
      return null;
    }
    if (result.getItems().size() > 1) {
      EasyCrudExceptionStrategy<TId, TRow> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(entityToSelect);

      IncorrectResultSizeDataAccessException t =
          new IncorrectResultSizeDataAccessException(1, result.getItems().size());
      throw exceptionStrategy.handleExceptionAtFind(t);
    }

    return result.getItems().get(0);
  }

  @Override
  public TRow getOne() {
    TRow ret = findOne();
    if (ret == null) {
      throw buildEntityNotFound();
    }
    return ret;
  }

  @Override
  public PaginatedList<TRow> find(PagerParams pagerParams, OrderBy... orderBy) {
    try {
      Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
      if (isGuaranteedToYieldEmptyResultset()) {
        return new PaginatedList<>(pagerParams, List.of(), 0);
      }

      EasyCrudWireTap<TRow> wireTap = querySpecificsResolver.getWireTap(entityToSelect);

      boolean requiresOnRead = wireTap.requiresOnRead();
      if (requiresOnRead) {
        wireTap.beforeRead();
      }

      PageLoadResults<TRow> result = loadPage(pagerParams, orderBy);
      int totalResultsCount = loadCount(pagerParams, result);
      PaginatedList<TRow> ret = new PaginatedList<>(pagerParams, result.list(), totalResultsCount);

      if (wireTap.requiresOnReadMultiple() && !result.list().isEmpty()) {
        wireTap.afterRead(result.list());
      }
      return ret;
    } catch (Throwable t) {
      EasyCrudExceptionStrategy<TId, TRow> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(entityToSelect);
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  protected PageLoadResults<TRow> loadPage(PagerParams pagerParams, OrderBy[] orderByInput) {
    OrderBy[] orderBy = ensureOrderByReferenceRegisteredQueries(orderByInput);

    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(joinQuery);

    List<Query<?, ?>> entitiesToSelect = List.of(entityToSelect);
    QueryData queryData =
        sqlBuilder.joinedSelect(joinQuery, entitiesToSelect, pagerParams, orderBy, fromAndWhere);

    if (areColumnsAliased(queryData)) {
      // aliases are used -- need the row mapping adapter
      ResultSetExtractorJoinedQueryImpl mappingContext =
          buildResultSetExtractor(entitiesToSelect, queryData);

      List<JoinedRow> list = jdbc.query(queryData.getSql(), queryData.getParams(), mappingContext);
      Preconditions.checkState(list != null, "List must not be null");

      return new PageLoadResults<>(
          fromAndWhere, list.stream().map(x -> x.get(entityToSelect)).toList());
    } else {
      List<TRow> list =
          jdbc.query(
              queryData.getSql(),
              queryData.getParams(),
              querySpecificsResolver.getRowMapper(entityToSelect));
      return new PageLoadResults<>(fromAndWhere, list);
    }
  }

  protected boolean areColumnsAliased(QueryData queryData) {
    for (ColumnsSelection columnsSelection : queryData.getSelectedColumns()) {
      if (columnsSelection.isWildcardAdded()) {
        return false;
      }
      if (CollectionUtils.isEmpty(columnsSelection.getColumns())) {
        continue;
      }
      if (columnsSelection.getColumns().stream()
          .anyMatch(c -> !Objects.equals(c.getColumnName(), c.getColumnLabel()))) {
        return true;
      }
    }
    return false;
  }

  protected int loadCount(PagerParams pagerParams, PageLoadResults<TRow> result) {
    int totalResultsCount;
    if (Top.is(pagerParams)
        || (PagerParams.ALL.equals(pagerParams)
            || (pagerParams.getOffset() == 0 && result.list().size() < pagerParams.getMax()))) {
      totalResultsCount = result.list().size();
    } else {
      QueryData countQueryData = sqlBuilder.countForJoinedQuery(result.fromAndWhere(), joinQuery);
      totalResultsCount = jdbc.queryForInt(countQueryData.getSql(), countQueryData.getParams());
    }
    return totalResultsCount;
  }

  @Override
  public List<TRow> findPage(PagerParams pagerParams, OrderBy... orderBy) {
    try {
      if (isGuaranteedToYieldEmptyResultset()) {
        return List.of();
      }

      Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
      EasyCrudWireTap<TRow> wireTap = querySpecificsResolver.getWireTap(entityToSelect);

      boolean requiresOnRead = wireTap.requiresOnRead();
      if (requiresOnRead) {
        wireTap.beforeRead();
      }

      List<TRow> ret = loadPage(pagerParams, orderBy).list();

      if (wireTap.requiresOnReadMultiple() && !ret.isEmpty()) {
        wireTap.afterRead(ret);
      }
      return ret;
    } catch (Throwable t) {
      EasyCrudExceptionStrategy<TId, TRow> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(entityToSelect);
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  @Override
  public int count() {
    if (isGuaranteedToYieldEmptyResultset()) {
      return 0;
    }

    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(joinQuery);
    QueryData countQueryData = sqlBuilder.countForJoinedQuery(fromAndWhere, joinQuery);
    return jdbc.queryForInt(countQueryData.getSql(), countQueryData.getParams());
  }

  @Override
  public TRow findFirst(OrderBy... orderBy) {
    PaginatedList<TRow> results = find(TOP_ONE, orderBy);
    if (results.getItems().isEmpty()) {
      return null;
    }
    return results.getItems().get(0);
  }

  @Override
  public TRow getFirst(OrderBy... orderBy) {
    TRow result = findFirst(orderBy);
    if (result == null) {
      throw buildEntityNotFound();
    }

    return result;
  }

  protected EntityNotFoundException buildEntityNotFound() {
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFieldConditionsToWhereClause(
        joinQuery.getQueries(), sql, new MapSqlParameterSource(), new ParamIdxIncrementer());
    return new EntityNotFoundException(
        querySpecificsResolver.getRowMessageCode(entityToSelect), "joinquery:" + sql);
  }

  @Override
  public List<TRow> findAll(OrderBy... orderBy) {
    return find(PagerParams.ALL, orderBy).getItems();
  }

  @Override
  public List<TRow> getAll(OrderBy... orderBy) {
    List<TRow> ret = findAll(orderBy);
    if (CollectionUtils.isEmpty(ret)) {
      throw buildEntityNotFound();
    }

    return ret;
  }
}
