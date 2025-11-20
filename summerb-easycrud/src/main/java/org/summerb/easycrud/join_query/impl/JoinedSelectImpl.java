package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinedSelect;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.impl.ParamIdxIncrementer;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;

@SuppressWarnings({"unchecked", "rawtypes", "SqlSourceToSinkFlow"})
public class JoinedSelectImpl extends SelectTemplate implements JoinedSelect {
  protected List<Query<?, ?>> entitiesToSelect;

  protected record PageLoadResults(
      FromAndWhere fromAndWhere,
      ResultSetExtractorJoinedQueryImpl mappingContext,
      List<JoinedRow> list) {}

  public JoinedSelectImpl(
      JoinQuery<?, ?> joinQuery,
      List<Query<?, ?>> entitiesToSelect,
      NamedParameterJdbcTemplateEx jdbc,
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      FieldsEnlister fieldsEnlister) {
    super(jdbc, joinQuery, querySpecificsResolver, sqlBuilder, fieldsEnlister);

    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(entitiesToSelect), "entitiesToSelect is required");

    this.entitiesToSelect = entitiesToSelect;
  }

  @Override
  public JoinedRow findOne() {
    PaginatedList<JoinedRow> result = find(TOP_TWO);
    if (!result.getHasItems()) {
      return null;
    }
    if (result.getItems().size() > 1) {
      EasyCrudExceptionStrategy<?, ?> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(joinQuery.getPrimaryQuery());

      IncorrectResultSizeDataAccessException t =
          new IncorrectResultSizeDataAccessException(1, result.getItems().size());
      throw exceptionStrategy.handleExceptionAtFind(t);
    }

    return result.getItems().get(0);
  }

  @Override
  public JoinedRow getOne() {
    JoinedRow ret = findOne();
    if (ret == null) {
      throw buildEntityNotFound();
    }
    return ret;
  }

  @Override
  public JoinedRow getFirst(OrderBy... orderBy) {
    JoinedRow result = findFirst(orderBy);
    if (result == null) {
      throw buildEntityNotFound();
    }

    return result;
  }

  @Override
  public JoinedRow findFirst(OrderBy... orderBy) {
    PaginatedList<JoinedRow> results = find(TOP_ONE, orderBy);
    if (results.getItems().isEmpty()) {
      return null;
    }
    return results.getItems().get(0);
  }

  @Override
  public PaginatedList<JoinedRow> find(PagerParams pagerParams, OrderBy... orderBy) {
    try {
      Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
      if (isGuaranteedToYieldEmptyResultset()) {
        return new PaginatedList<>(pagerParams, List.of(), 0);
      }

      // Before query wiretaps
      Map<Query<?, ?>, EasyCrudWireTap> wireTaps = findWireTapsOnRead();
      wireTaps.values().forEach(EasyCrudWireTap::beforeRead);

      // Query itself
      ResultSetExtractorJoinedQueryImpl result = queryPageAndTotal(pagerParams, orderBy);

      // After query wiretaps
      for (Map.Entry<Query<?, ?>, EasyCrudWireTap> entry : wireTaps.entrySet()) {
        Map<?, ? extends HasId> rows = result.getMappedRows(entry.getKey());

        if (entry.getValue().requiresOnReadMultiple() && !rows.isEmpty()) {
          List es = new ArrayList<>(rows.values());
          entry.getValue().afterRead(es);
        }
      }

      // return
      return new PaginatedList<>(pagerParams, result.rows, result.totalResultsCount);
    } catch (Throwable t) {
      // At this point we do not really know which entity this exception is relevant to. So we just
      // pick the first query
      EasyCrudExceptionStrategy<?, ?> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(joinQuery.getPrimaryQuery());
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  protected ResultSetExtractorJoinedQueryImpl queryPageAndTotal(
      PagerParams pagerParams, OrderBy[] orderByInput) {

    PageLoadResults result = loadPage(pagerParams, orderByInput);

    loadCount(pagerParams, result);

    return result.mappingContext();
  }

  private void loadCount(PagerParams pagerParams, PageLoadResults result) {
    if (Top.is(pagerParams)
        || (PagerParams.ALL.equals(pagerParams)
            || (pagerParams.getOffset() == 0 && result.list().size() < pagerParams.getMax()))) {
      result.mappingContext().setTotalResultsCount(result.list().size());
    } else {
      QueryData countQueryData = sqlBuilder.countForJoinedQuery(result.fromAndWhere(), joinQuery);
      result
          .mappingContext()
          .setTotalResultsCount(
              jdbc.queryForInt(countQueryData.getSql(), countQueryData.getParams()));
    }
  }

  protected PageLoadResults loadPage(PagerParams pagerParams, OrderBy[] orderByInput) {
    OrderBy[] orderBy = ensureOrderByReferenceRegisteredQueries(orderByInput);

    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(joinQuery);

    QueryData queryData =
        sqlBuilder.joinedMultipleTablesMultipleRows(
            joinQuery, entitiesToSelect, pagerParams, orderBy, fromAndWhere);

    ResultSetExtractorJoinedQueryImpl mappingContext = buildResultSetExtractor(queryData);

    List<JoinedRow> list = jdbc.query(queryData.getSql(), queryData.getParams(), mappingContext);
    Preconditions.checkState(list != null, "List must not be null");
    return new PageLoadResults(fromAndWhere, mappingContext, list);
  }

  @Override
  public List<JoinedRow> findPage(PagerParams pagerParams, OrderBy... orderBy) {
    try {
      if (isGuaranteedToYieldEmptyResultset()) {
        return List.of();
      }

      Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");

      // Before query wiretaps
      Map<Query<?, ?>, EasyCrudWireTap> wireTaps = findWireTapsOnRead();
      wireTaps.values().forEach(EasyCrudWireTap::beforeRead);

      // Query itself
      ResultSetExtractorJoinedQueryImpl result = loadPage(pagerParams, orderBy).mappingContext;

      // After query wiretaps
      for (Map.Entry<Query<?, ?>, EasyCrudWireTap> entry : wireTaps.entrySet()) {
        Map<?, ? extends HasId> rows = result.getMappedRows(entry.getKey());

        if (entry.getValue().requiresOnReadMultiple() && !rows.isEmpty()) {
          List es = new ArrayList<>(rows.values());
          entry.getValue().afterRead(es);
        }
      }

      // return
      return result.rows;
    } catch (Throwable t) {
      // At this point we do not really know which entity this exception is relevant to. So we just
      // pick the first query
      EasyCrudExceptionStrategy<?, ?> exceptionStrategy =
          querySpecificsResolver.getExceptionStrategy(joinQuery.getPrimaryQuery());
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  protected ResultSetExtractorJoinedQueryImpl buildResultSetExtractor(QueryData queryData) {
    return new ResultSetExtractorJoinedQueryImpl(
        entitiesToSelect, queryData.getSelectedColumns(), querySpecificsResolver);
  }

  protected Map<Query<?, ?>, EasyCrudWireTap> findWireTapsOnRead() {
    Map<Query<?, ?>, EasyCrudWireTap> wireTaps = new HashMap<>();
    for (Query<?, ?> query : entitiesToSelect) {
      EasyCrudWireTap wireTap = querySpecificsResolver.getWireTap(query);
      if (wireTap.requiresOnRead()) {
        wireTaps.put(query, wireTap);
      }
    }
    return wireTaps;
  }

  @Override
  public List<JoinedRow> findAll(OrderBy... orderBy) {
    return find(PagerParams.ALL, orderBy).getItems();
  }

  @Override
  public List<JoinedRow> getAll(OrderBy... orderBy) {
    List<JoinedRow> ret = findAll(orderBy);
    if (CollectionUtils.isEmpty(ret)) {
      throw buildEntityNotFound();
    }

    return ret;
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

  protected EntityNotFoundException buildEntityNotFound() {
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFieldConditionsToWhereClause(
        joinQuery.getQueries(), sql, new MapSqlParameterSource(), new ParamIdxIncrementer());
    return new EntityNotFoundException(
        querySpecificsResolver.getRowMessageCode(joinQuery.getPrimaryQuery()), "joinquery:" + sql);
  }
}
