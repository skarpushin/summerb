/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.dao;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.dao.SimpleJdbcUpdate.SimpleJdbcUpdate;
import org.summerb.easycrud.dao.SimpleJdbcUpdate.TableMetaDataContext;
import org.summerb.easycrud.dao.SimpleJdbcUpdate.UpdateColumnsEnlisterStrategy;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.exceptions.DaoExceptionTranslatorMySqlImpl;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasAuthor;
import org.summerb.easycrud.row.HasAutoincrementId;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.row.HasTimestamps;
import org.summerb.easycrud.row.HasUuid;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;
import org.summerb.easycrud.tools.StringIdGenerator;
import org.summerb.easycrud.tools.StringIdGeneratorUuidImpl;
import org.summerb.utils.clock.ClockResolver;
import org.summerb.utils.clock.ClockResolverImpl;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;

/**
 * Although this is a MySQL-specific impl of {@link EasyCrudDao} it has common things for all
 * SQL-like databases.
 *
 * @author sergey.karpushin
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class EasyCrudDaoSqlImpl<TId, TRow extends HasId<TId>> extends TableDaoBase
    implements EasyCrudDao<TId, TRow>, EasyCrudDaoInjections<TId, TRow> {

  protected Class<TRow> rowClass;
  protected SqlBuilder sqlBuilder;

  protected RowMapper<TRow> rowMapper;
  protected SqlTypeOverrides sqlTypeOverrides;
  protected ParameterSourceBuilder<TRow> parameterSourceBuilder;
  protected ConversionService conversionService;
  protected StringIdGenerator stringIdGenerator;
  protected DaoExceptionTranslator daoExceptionTranslator;
  protected ClockResolver clockResolver;

  protected SimpleJdbcInsert jdbcInsert;
  protected SimpleJdbcUpdate jdbcUpdate;

  /**
   * Constructor for cases when subclass wants to take full responsibility on instantiation process.
   *
   * @deprecated when using this constructor please make sure you're properly initializing required
   *     dependencies: {@link #dataSource}, {@link #tableName}, {@link #rowClass} and {@link
   *     #sqlBuilder}
   */
  @Deprecated
  @SuppressWarnings("DeprecatedIsStillUsed")
  protected EasyCrudDaoSqlImpl() {}

  public EasyCrudDaoSqlImpl(DataSource dataSource, String tableName, Class<TRow> rowClass) {
    super(dataSource, tableName);
    Preconditions.checkArgument(rowClass != null, "rowClass required");

    this.rowClass = rowClass;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    Preconditions.checkState(rowClass != null, "rowClass required");
    Preconditions.checkState(sqlBuilder != null, "sqlBuilder required");

    if (rowMapper == null) {
      rowMapper = buildDefaultRowMapper(this.rowClass, this.conversionService);
    }

    if (sqlTypeOverrides == null) {
      sqlTypeOverrides = new SqlTypeOverridesDefaultImpl();
    }

    if (parameterSourceBuilder == null) {
      parameterSourceBuilder = buildDefaultParameterSourceBuilder();
    }

    if (daoExceptionTranslator == null) {
      daoExceptionTranslator = buildDefaultDaoExceptionToFveTranslator();
    }

    if (HasUuid.class.isAssignableFrom(rowClass) && stringIdGenerator == null) {
      stringIdGenerator = buildDefaultStringIdGenerator();
    }

    if (HasTimestamps.class.isAssignableFrom(rowClass) && clockResolver == null) {
      clockResolver = buildDefaultClockResolver();
    }

    jdbcInsert = buildJdbcInsert();
    jdbcUpdate = initJdbcUpdate();
  }

  protected ClockResolver buildDefaultClockResolver() {
    return new ClockResolverImpl();
  }

  protected StringIdGeneratorUuidImpl buildDefaultStringIdGenerator() {
    return new StringIdGeneratorUuidImpl();
  }

  protected DaoExceptionTranslatorMySqlImpl buildDefaultDaoExceptionToFveTranslator() {
    return new DaoExceptionTranslatorMySqlImpl();
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
    restrictingColumns.add(QueryToSqlMySqlImpl.snakeCase(HasId.FN_ID));
    if (HasTimestamps.class.isAssignableFrom(rowClass)) {
      restrictingColumns.add(QueryToSqlMySqlImpl.snakeCase(HasTimestamps.FN_MODIFIED_AT));
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
            remove(QueryToSqlMySqlImpl.snakeCase(HasTimestamps.FN_CREATED_AT), updatingColumns);
          }
          if (HasAuthor.class.isAssignableFrom(rowClass)) {
            remove(QueryToSqlMySqlImpl.snakeCase(HasAuthor.FN_CREATED_BY), updatingColumns);
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
    if (row instanceof HasUuid hasUuid) {
      if (!stringIdGenerator.isValidId(hasUuid.getId())) {
        hasUuid.setId(stringIdGenerator.generateNewId(row));
      }
    }

    if (row instanceof HasTimestamps hasTimestamps) {
      long now = clockResolver.clock().millis();
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
    if (row instanceof HasTimestamps hasTimestamps) {
      long modifiedAt = hasTimestamps.getModifiedAt();
      hasTimestamps.setModifiedAt(clockResolver.clock().millis());
      restrictionParams.addValue(HasTimestamps.FN_MODIFIED_AT, modifiedAt);
    }

    SqlParameterSource rowParams = parameterSourceBuilder.buildParameterSource(row);

    try {
      int affectedRows = jdbcUpdate.execute(rowParams, restrictionParams);
      if (affectedRows != 1) {
        throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
            jdbcUpdate.getUpdateString(), 1, affectedRows);
      }
      return affectedRows;
    } catch (Throwable t) {
      daoExceptionTranslator.translateAndThrowIfApplicable(t);
      throw t;
    }
  }

  @Override
  public TRow findById(TId id) {
    QueryData queryData = sqlBuilder.findById(tableName, id);

    try {
      return jdbc.queryForObject(queryData.getSql(), queryData.getParams(), rowMapper);
    } catch (EmptyResultDataAccessException e) {
      // not logging exception because method is findXXX - hence by convention we just return null
      // result - this is by design
      return null;
    }
  }

  @Override
  public int delete(TId id) {
    QueryData queryData = sqlBuilder.deleteById(tableName, id);
    return jdbc.update(queryData.getSql(), queryData.getParams());
  }

  @Override
  public int delete(TId id, long modifiedAt) {
    QueryData queryData = sqlBuilder.deleteByIdOptimistic(tableName, id, modifiedAt);

    int affectedRows = jdbc.update(queryData.getSql(), queryData.getParams());
    if (affectedRows != 1) {
      throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
          queryData.getSql(), 1, affectedRows);
    }
    return affectedRows;
  }

  @Override
  public TRow findOneByQuery(Query<TId, TRow> query) {
    Preconditions.checkNotNull(query, "query required");
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(query.getConditions()), "query.conditions required");

    QueryData queryData = sqlBuilder.selectSingleRow(tableName, query);

    try {
      return jdbc.queryForObject(queryData.getSql(), queryData.getParams(), rowMapper);
    } catch (EmptyResultDataAccessException e) {
      // not logging exception because method is findXXX - hence by convention we just return null
      // result - this is by design
      return null;
    }
  }

  @Override
  public int deleteByQuery(Query<TId, TRow> query) {
    QueryData queryData = sqlBuilder.deleteByQuery(tableName, query);
    return jdbc.update(queryData.getSql(), queryData.getParams());
  }

  @Override
  public PaginatedList<TRow> query(
      PagerParams pagerParams, Query<TId, TRow> optionalQuery, OrderBy... orderBy) {

    boolean countQueryNeeded = isCountQueryNeeded(pagerParams);

    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(tableName, optionalQuery);
    QueryData dataQuery =
        sqlBuilder.select(
            rowClass, fromAndWhere, optionalQuery, pagerParams, orderBy, countQueryNeeded);

    List<TRow> list = jdbc.query(dataQuery.getSql(), dataQuery.getParams(), rowMapper);

    int totalResultsCount;
    if (countQueryNeeded) {
      QueryData countQuery = sqlBuilder.queryForCountAfterPagedSelect(fromAndWhere);
      totalResultsCount = jdbc.queryForInt(countQuery.getSql(), countQuery.getParams());
    } else {
      totalResultsCount = list.size();
    }

    return new PaginatedList<>(pagerParams, list, totalResultsCount);
  }

  @Override
  public List<TRow> queryPage(
      PagerParams pagerParams, Query<TId, TRow> optionalQuery, OrderBy[] orderBy) {
    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(tableName, optionalQuery);
    QueryData dataQuery =
        sqlBuilder.select(rowClass, fromAndWhere, optionalQuery, pagerParams, orderBy, false);

    return jdbc.query(dataQuery.getSql(), dataQuery.getParams(), rowMapper);
  }

  @Override
  public int count(Query<TId, TRow> optionalQuery) {
    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(tableName, optionalQuery);
    QueryData countQuery = sqlBuilder.countForSimpleSelect(fromAndWhere);
    return jdbc.queryForInt(countQuery.getSql(), countQuery.getParams());
  }

  protected static boolean isCountQueryNeeded(PagerParams pagerParams) {
    if (Top.is(pagerParams) || PagerParams.ALL.equals(pagerParams)) {
      return false;
    } else {
      return true;
    }
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
   *     #update(HasId)}, {@link #query(PagerParams, Query, OrderBy...)} (and other methods which
   *     uses {@link Query})
   */
  @Autowired(required = false)
  public void setSqlTypeOverrides(SqlTypeOverrides sqlTypeOverrides) {
    this.sqlTypeOverrides = sqlTypeOverrides;
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
    Preconditions.checkState(rowClass != null, "please set rowClass before calling this method");
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
  public ClockResolver getClockResolver() {
    return clockResolver;
  }

  /**
   * Set {@link ClockResolver}. This is required only when Row class implements {@link
   * HasTimestamps}. If nothing set, then default {@link ClockResolverImpl} will be used
   *
   * @param clockResolver clockResolver
   */
  @Autowired(required = false)
  public void setClockResolver(ClockResolver clockResolver) {
    Preconditions.checkState(rowClass != null, "please set rowClass before calling this method");
    Preconditions.checkArgument(
        !HasTimestamps.class.isAssignableFrom(rowClass) || clockResolver != null,
        "clockResolver required");
    this.clockResolver = clockResolver;
  }

  @Override
  public SqlBuilder getSqlBuilder() {
    return sqlBuilder;
  }

  @Autowired(required = false)
  public void setSqlBuilder(SqlBuilder sqlBuilder) {
    this.sqlBuilder = sqlBuilder;
  }
}
