/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.impl.dao.mysql;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.underscore;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.DaoExceptionTranslator;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.api.row.HasAuthor;
import org.summerb.easycrud.api.row.HasAutoincrementId;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.easycrud.api.row.HasUuid;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.SimpleJdbcUpdate;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.TableMetaDataContext;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.UpdateColumnsEnlisterStrategy;
import org.summerb.easycrud.impl.StringIdGeneratorUuidImpl;
import org.summerb.easycrud.impl.dao.ParameterSourceBuilderBeanPropImpl;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;
import org.summerb.easycrud.impl.dao.SqlTypeOverridesDefaultImpl;
import org.summerb.easycrud.impl.dao.TableDaoBase;
import org.summerb.utils.clock.NowResolver;
import org.summerb.utils.clock.NowResolverImpl;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;

/**
 * Although this is a MySQL-specific impl of {@link EasyCrudDao} it has common things for all
 * SQL-like databases.
 *
 * @author sergey.karpushin
 */
public class EasyCrudDaoSqlImpl<TId, TRow extends HasId<TId>> extends TableDaoBase
    implements EasyCrudDao<TId, TRow>, EasyCrudDaoInjections<TId, TRow> {
  protected static final List<String> allowedSortDirections = Arrays.asList("asc", "desc");
  protected static final String PARAM_MAX = "max";
  protected static final String PARAM_OFFSET = "offset";

  protected Class<TRow> rowClass;

  protected RowMapper<TRow> rowMapper;
  protected SqlTypeOverrides sqlTypeOverrides;
  protected ParameterSourceBuilder<TRow> parameterSourceBuilder;
  protected ConversionService conversionService;
  protected QueryToSql queryToSql;
  protected StringIdGenerator stringIdGenerator;
  protected DaoExceptionTranslator daoExceptionTranslator;
  protected NowResolver nowResolver;

  protected SimpleJdbcInsert jdbcInsert;
  protected SimpleJdbcUpdate jdbcUpdate;
  protected String sqlFindById;
  protected String sqlDeleteById;
  protected String sqlDeleteOptimisticById;
  protected String sqlFindByCustomQuery;
  protected String sqlDeleteByCustomQuery;
  protected String sqlFindByCustomQueryForCount;
  protected String sqlPartPaginator;

  /**
   * Constructor for cases when sub-class wants to take full responsibility on instantiation
   * process.
   *
   * @deprecated when using this constructor please make sure you're properly initializing required
   *     dependencies: {@link #dataSource}, {@link #tableName} and {@link #rowClass}
   */
  @Deprecated
  @SuppressWarnings("DeprecatedIsStillUsed")
  protected EasyCrudDaoSqlImpl() {}

  public EasyCrudDaoSqlImpl(DataSource dataSource, String tableName, Class<TRow> rowClass) {
    super(dataSource, tableName);
    this.rowClass = rowClass;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    Preconditions.checkArgument(rowClass != null, "rowClass required");

    if (rowMapper == null) {
      rowMapper = buildDefaultRowMapper(this.rowClass, this.conversionService);
    }

    if (sqlTypeOverrides == null) {
      sqlTypeOverrides = new SqlTypeOverridesDefaultImpl();
    }

    if (parameterSourceBuilder == null) {
      parameterSourceBuilder = buildDefaultParameterSourceBuilder();
    }

    if (queryToSql == null) {
      queryToSql = buildDefaultQueryToNativeSqlCompiler();
    }

    if (daoExceptionTranslator == null) {
      daoExceptionTranslator = buildDefaultDaoExceptionToFveTranslator();
    }

    if (HasUuid.class.isAssignableFrom(rowClass) && stringIdGenerator == null) {
      stringIdGenerator = buildDefaultStringIdGenerator();
    }

    if (HasTimestamps.class.isAssignableFrom(rowClass) && nowResolver == null) {
      nowResolver = buildDefaultNowResolver();
    }

    jdbcInsert = buildJdbcInsert();
    jdbcUpdate = initJdbcUpdate();
    buildSqlQueries();
  }

  protected NowResolver buildDefaultNowResolver() {
    return new NowResolverImpl();
  }

  protected StringIdGeneratorUuidImpl buildDefaultStringIdGenerator() {
    return new StringIdGeneratorUuidImpl();
  }

  protected DaoExceptionTranslatorMySqlImpl buildDefaultDaoExceptionToFveTranslator() {
    return new DaoExceptionTranslatorMySqlImpl();
  }

  protected QueryToSqlMySqlImpl buildDefaultQueryToNativeSqlCompiler() {
    return new QueryToSqlMySqlImpl(sqlTypeOverrides);
  }

  protected ParameterSourceBuilderBeanPropImpl<TRow> buildDefaultParameterSourceBuilder() {
    return new ParameterSourceBuilderBeanPropImpl<>(sqlTypeOverrides, rowClass);
  }

  protected RowMapper<TRow> buildDefaultRowMapper(
      Class<TRow> rowClass, ConversionService conversionService) {
    BeanPropertyRowMapper<TRow> ret = new BeanPropertyRowMapper<>(rowClass);

    if (conversionService != null) {
      ret.setConversionService(conversionService);
    }

    return ret;
  }

  protected void buildSqlQueries() {
    sqlFindById =
        String.format("SELECT %s FROM %s WHERE id = :id", buildFieldsForSelect(), tableName);
    sqlDeleteById = String.format("DELETE FROM %s WHERE id = :id", tableName);
    sqlDeleteByCustomQuery = String.format("DELETE FROM %s WHERE ", tableName);
    sqlDeleteOptimisticById =
        String.format("DELETE FROM %s WHERE id = :id AND modified_at = :modifiedAt", tableName);
    sqlFindByCustomQuery = String.format("SELECT %s FROM %s ", buildFieldsForSelect(), tableName);
    sqlFindByCustomQueryForCount = String.format("SELECT count(*) FROM %s ", tableName);
    sqlPartPaginator = " LIMIT :max OFFSET :offset";
  }

  protected String buildFieldsForSelect() {
    return "*";
  }

  protected SimpleJdbcInsert buildJdbcInsert() {
    SimpleJdbcInsert ret = new SimpleJdbcInsert(dataSource).withTableName(tableName);
    if (HasAutoincrementId.class.isAssignableFrom(rowClass)) {
      ret = ret.usingGeneratedKeyColumns(HasId.FN_ID);
    }
    return ret;
  }

  protected SimpleJdbcUpdate initJdbcUpdate() {
    SimpleJdbcUpdate ret = new SimpleJdbcUpdate(dataSource).withTableName(tableName);

    // Configure identification columns - how do we find right record for
    // update
    List<String> restrictingColumns = new ArrayList<>();
    restrictingColumns.add(QueryToSqlMySqlImpl.underscore(HasId.FN_ID));
    if (HasTimestamps.class.isAssignableFrom(rowClass)) {
      restrictingColumns.add(QueryToSqlMySqlImpl.underscore(HasTimestamps.FN_MODIFIED_AT));
    }
    ret.setRestrictingColumns(restrictingColumns);

    ret.setUpdateColumnsEnlisterStrategy(updateColumnsEnlisterStrategy);
    return ret;
  }

  protected UpdateColumnsEnlisterStrategy updateColumnsEnlisterStrategy =
      new UpdateColumnsEnlisterStrategy() {
        @Override
        public Collection<? extends String> getColumnsForUpdate(
            TableMetaDataContext tableMetaDataContext) {
          List<String> updatingColumns = new ArrayList<>(tableMetaDataContext.createColumns());
          remove(HasId.FN_ID, updatingColumns);
          if (HasTimestamps.class.isAssignableFrom(rowClass)) {
            remove(QueryToSqlMySqlImpl.underscore(HasTimestamps.FN_CREATED_AT), updatingColumns);
          }
          if (HasAuthor.class.isAssignableFrom(rowClass)) {
            remove(QueryToSqlMySqlImpl.underscore(HasAuthor.FN_CREATED_BY), updatingColumns);
          }
          return updatingColumns;
        }

        private void remove(String str, Iterable<String> iterable) {
          for (Iterator<String> iter = iterable.iterator(); iter.hasNext(); ) {
            if (str.equalsIgnoreCase(iter.next())) {
              iter.remove();
            }
          }
        }
      };

  @Override
  public void create(TRow row) {
    if (row instanceof HasUuid) {
      HasUuid hasUuid = (HasUuid) row;
      if (!stringIdGenerator.isValidId(hasUuid.getId())) {
        hasUuid.setId(stringIdGenerator.generateNewId(row));
      }
    }

    if (row instanceof HasTimestamps) {
      HasTimestamps hasTimestamps = (HasTimestamps) row;
      long now = nowResolver.clock().millis();
      hasTimestamps.setCreatedAt(now);
      hasTimestamps.setModifiedAt(now);
    }

    SqlParameterSource params = parameterSourceBuilder.buildParameterSource(row);
    try {
      if (row instanceof HasAutoincrementId) {
        Number id = jdbcInsert.executeAndReturnKey(params);
        ((HasAutoincrementId) row).setId(id.longValue());
      } else {
        jdbcInsert.execute(params);
      }
    } catch (Throwable t) {
      daoExceptionTranslator.translateAndThrowIfApplicable(t);
      throw t;
    }
  }

  @Override
  public int update(TRow row) {
    MapSqlParameterSource restrictionParams = new MapSqlParameterSource();
    restrictionParams.addValue(HasId.FN_ID, row.getId());
    if (row instanceof HasTimestamps) {
      HasTimestamps hasTimestamps = (HasTimestamps) row;
      long modifiedAt = hasTimestamps.getModifiedAt();
      hasTimestamps.setModifiedAt(nowResolver.clock().millis());
      restrictionParams.addValue(HasTimestamps.FN_MODIFIED_AT, modifiedAt);
    }

    SqlParameterSource rowParams = parameterSourceBuilder.buildParameterSource(row);

    try {
      return jdbcUpdate.execute(rowParams, restrictionParams);
    } catch (Throwable t) {
      daoExceptionTranslator.translateAndThrowIfApplicable(t);
      throw t;
    }
  }

  @Override
  public TRow findById(TId id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);

    try {
      return jdbc.queryForObject(sqlFindById, params, rowMapper);
    } catch (EmptyResultDataAccessException e) {
      // not logging exception because method is findXXX - hence by convention we just return null
      // result - this is by design
      return null;
    }
  }

  @Override
  public int delete(TId id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);
    return jdbc.update(sqlDeleteById, params);
  }

  @Override
  public int delete(TId id, long modifiedAt) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(HasId.FN_ID, id);
    params.addValue(HasTimestamps.FN_MODIFIED_AT, modifiedAt);
    return jdbc.update(sqlDeleteOptimisticById, params);
  }

  @Override
  public TRow findOneByQuery(QueryConditions query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    String whereClause = queryToSql.buildWhereClauseAndPopulateParams(query, params);

    try {
      return jdbc.queryForObject(sqlFindByCustomQuery + "WHERE " + whereClause, params, rowMapper);
    } catch (EmptyResultDataAccessException e) {
      // not logging exception because method is findXXX - hence by convention we just return null
      // result - this is by design
      return null;
    }
  }

  @Override
  public int deleteByQuery(QueryConditions query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    String whereClause = queryToSql.buildWhereClauseAndPopulateParams(query, params);
    return jdbc.update(sqlDeleteByCustomQuery + whereClause, params);
  }

  @Override
  public PaginatedList<TRow> query(
      PagerParams pagerParams, QueryConditions optionalQuery, OrderBy... orderBy) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    String whereClause =
        optionalQuery == null || optionalQuery.isEmpty()
            ? ""
            : "WHERE " + queryToSql.buildWhereClauseAndPopulateParams(optionalQuery, params);
    params.addValue(PARAM_OFFSET, pagerParams.getOffset());
    params.addValue(PARAM_MAX, pagerParams.getMax());

    String query = sqlFindByCustomQuery + whereClause + buildOrderBySubclause(orderBy);
    if (!PagerParams.ALL.equals(pagerParams)) {
      query = query + sqlPartPaginator;
    }
    List<TRow> list = jdbc.query(query, params, rowMapper);

    int totalResultsCount;
    if (Top.is(pagerParams)
        || (PagerParams.ALL.equals(pagerParams)
            || (pagerParams.getOffset() == 0 && list.size() < pagerParams.getMax()))) {
      totalResultsCount = list.size();
    } else {
      // TODO: For MySQL we can use combination of SQL_CALC_FOUND_ROWS and
      // FOUND_ROWS() to improve performance -- but this is MySQL specific
      // functionality
      totalResultsCount = jdbc.queryForInt(sqlFindByCustomQueryForCount + whereClause, params);
    }

    return new PaginatedList<>(pagerParams, list, totalResultsCount);
  }

  protected String buildOrderBySubclause(OrderBy[] orderByArr) {
    if (orderByArr == null || orderByArr.length == 0) {
      return "";
    }

    StringBuilder ret = new StringBuilder();
    for (OrderBy orderBy : orderByArr) {
      if (ret.length() > 0) {
        ret.append(", ");
      }

      if (orderBy == null
          || !StringUtils.hasText(orderBy.getDirection())
          || !StringUtils.hasText(orderBy.getFieldName())) {
        continue;
      }

      ret.append(underscore(orderBy.getFieldName()));
      ret.append(" ");
      Preconditions.checkArgument(
          allowedSortDirections.contains(orderBy.getDirection().toLowerCase()),
          "OrderBy not allowed: %s",
          orderBy.getDirection());
      ret.append(orderBy.getDirection());
    }
    return ret.length() == 0 ? "" : " ORDER BY " + ret;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public NamedParameterJdbcTemplate getJdbc() {
    return jdbc;
  }

  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  @Override
  public Class<TRow> getRowClass() {
    return rowClass;
  }

  @Override
  public RowMapper<TRow> getRowMapper() {
    return rowMapper;
  }

  /**
   * Set {@link RowMapper}. Optional. if nothing set, then {@link BeanPropertyRowMapper} will be
   * used
   *
   * @param rowMapper rowMapper
   */
  public void setRowMapper(RowMapper<TRow> rowMapper) {
    Preconditions.checkArgument(rowMapper != null, "parameterSourceBuilder required");
    this.rowMapper = rowMapper;
  }

  @Override
  public ParameterSourceBuilder<TRow> getParameterSourceBuilder() {
    return parameterSourceBuilder;
  }

  /**
   * Set {@link ParameterSourceBuilder}. Optional. If nothing set, then {@link
   * ParameterSourceBuilderBeanPropImpl} will be used
   *
   * @param parameterSourceBuilder parameterSourceBuilder
   */
  public void setParameterSourceBuilder(ParameterSourceBuilder<TRow> parameterSourceBuilder) {
    Preconditions.checkArgument(parameterSourceBuilder != null, "parameterSourceBuilder required");
    this.parameterSourceBuilder = parameterSourceBuilder;
  }

  @Override
  public SqlTypeOverrides getSqlTypeOverrides() {
    return sqlTypeOverrides;
  }

  /**
   * When your DTO has fields of custom types you'll need to explicitly tell DAO layer what SQL
   * types must be used and how to convert values of those types to SQL-friendly values. Use this
   * class to describe such conversion.
   *
   * @param sqlTypeOverrides overrides for SQL types for {@link #create(HasId)}, {@link
   *     #update(HasId)}, {@link #query(PagerParams, QueryConditions, OrderBy...)} (and other
   *     methods which uses {@link Query})
   */
  @Autowired(required = false)
  public void setSqlTypeOverrides(SqlTypeOverrides sqlTypeOverrides) {
    this.sqlTypeOverrides = sqlTypeOverrides;
  }

  @Override
  public QueryToSql getQueryToSql() {
    return queryToSql;
  }

  /**
   * Set {@link QueryToSql}. Optionally autowired from application context. If nothing set, then
   * {@link QueryToSqlMySqlImpl} will be used
   *
   * @param queryToSql queryToSql
   */
  @Autowired(required = false)
  public void setQueryToSql(QueryToSql queryToSql) {
    Preconditions.checkArgument(queryToSql != null, "queryToSql required");
    this.queryToSql = queryToSql;
  }

  @Override
  public ConversionService getConversionService() {
    return conversionService;
  }

  /**
   * Set {@link ConversionService}. Optionally autowired from application context. if nothing set,
   * then nothing used.
   *
   * @param conversionService conversionService
   */
  @Autowired(required = false)
  public void setConversionService(ConversionService conversionService) {
    Preconditions.checkArgument(conversionService != null, "conversionService required");
    this.conversionService = conversionService;
  }

  @Override
  public StringIdGenerator getStringIdGenerator() {
    return stringIdGenerator;
  }

  /**
   * Set {@link StringIdGenerator}. Required only when rowClass implements {@link HasUuid}. If
   * nothing set, then {@link StringIdGeneratorUuidImpl} will be used.
   *
   * @param stringIdGenerator stringIdGenerator
   */
  public void setStringIdGenerator(StringIdGenerator stringIdGenerator) {
    Preconditions.checkState(rowClass != null, "please set rowClass before callign this method");
    Preconditions.checkArgument(
        !HasUuid.class.isAssignableFrom(rowClass) || stringIdGenerator != null,
        "stringIdGenerator required");
    this.stringIdGenerator = stringIdGenerator;
  }

  @Override
  public DaoExceptionTranslator getDaoExceptionTranslator() {
    return daoExceptionTranslator;
  }

  /**
   * Set {@link DaoExceptionTranslator}. Optionally autowired from application context. if nothing
   * set, then default {@link DaoExceptionTranslatorMySqlImpl} will be used
   *
   * @param daoExceptionTranslator daoExceptionTranslator
   */
  @Autowired(required = false)
  public void setDaoExceptionTranslator(DaoExceptionTranslator daoExceptionTranslator) {
    Preconditions.checkArgument(daoExceptionTranslator != null, "daoExceptionTranslator required");
    this.daoExceptionTranslator = daoExceptionTranslator;
  }

  @Override
  public NowResolver getNowResolver() {
    return nowResolver;
  }

  /**
   * Set {@link NowResolver}. This is required only when Row class implements {@link HasTimestamps}.
   * If nothing set, then default {@link NowResolverImpl} will be used
   *
   * @param nowResolver nowResolver
   */
  @Autowired(required = false)
  public void setNowResolver(NowResolver nowResolver) {
    Preconditions.checkState(rowClass != null, "please set rowClass before callign this method");
    Preconditions.checkArgument(
        !HasTimestamps.class.isAssignableFrom(rowClass) || nowResolver != null,
        "nowResolver required");
    this.nowResolver = nowResolver;
  }
}
