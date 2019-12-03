/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.easycrud.impl.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.DaoExceptionToFveTranslator;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.api.QueryToNativeSqlCompiler;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.dto.HasAuthor;
import org.summerb.easycrud.api.dto.HasAutoincrementId;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.HasTimestamps;
import org.summerb.easycrud.api.dto.HasUuid;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.dto.Top;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.common.DaoBase;
import org.summerb.easycrud.impl.ParameterSourceBuilderBeanPropImpl;
import org.summerb.easycrud.impl.StringIdGeneratorUuidImpl;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.SimpleJdbcUpdate;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.TableMetaDataContext;
import org.summerb.easycrud.impl.SimpleJdbcUpdate.UpdateColumnsEnlisterStrategy;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;

/**
 * Although this is a MySQL-specific impl of {@link EasyCrudDao} it has common
 * things for all SQL-like databases.
 * 
 * TBD: Change sql statements fields visibility to simplify
 * customization-by-inheritance
 * 
 * @author sergey.karpushin
 *
 */
public class EasyCrudDaoMySqlImpl<TId, TDto extends HasId<TId>> extends DaoBase
		implements EasyCrudDao<TId, TDto>, InitializingBean {
	protected static final List<String> allowedSortDirections = Arrays.asList("asc", "desc");

	private String tableName;
	private Class<TDto> dtoClass;
	private RowMapper<TDto> rowMapper;
	private ParameterSourceBuilder<TDto> parameterSourceBuilder;
	private ConversionService conversionService;
	private QueryToNativeSqlCompiler queryToNativeSqlCompiler = new QueryToNativeSqlCompilerMySqlImpl();
	private StringIdGenerator stringIdGenerator = new StringIdGeneratorUuidImpl();
	private DaoExceptionToFveTranslator daoExceptionToFveTranslator = new DaoExceptionToFveTranslatorMySqlImpl();

	protected SimpleJdbcInsert jdbcInsert;
	protected SimpleJdbcUpdate jdbcUpdate;

	protected String sqlFindById;
	protected String sqlDeleteById;
	protected String sqlDeleteOptimisticById;
	protected String sqlFindByCustomQuery;
	protected String sqlDeleteByCustomQuery;
	protected String sqlFindByCustomQueryForCount;
	protected String sqlPartPaginator;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(StringUtils.hasText(tableName), "TableName required");
		Preconditions.checkState(dtoClass != null, "DtoClass required");

		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<TDto>(dtoClass);
			if (conversionService != null) {
				((BeanPropertyRowMapper<TDto>) rowMapper).setConversionService(conversionService);
			}
		}

		jdbcInsert = buildJdbcInsert();
		jdbcUpdate = initJdbcUpdate();

		if (parameterSourceBuilder == null) {
			parameterSourceBuilder = new ParameterSourceBuilderBeanPropImpl<TDto>();
		}

		buildSqlQueries();
	}

	protected void buildSqlQueries() {
		sqlFindById = String.format("SELECT %s FROM %s WHERE id = :id", buildFieldsForSelect(), tableName);
		sqlDeleteById = String.format("DELETE FROM %s WHERE id = :id", tableName);
		sqlDeleteByCustomQuery = String.format("DELETE FROM %s WHERE ", tableName);
		sqlDeleteOptimisticById = String.format("DELETE FROM %s WHERE id = :id AND modified_at = :modifiedAt",
				tableName);
		sqlFindByCustomQuery = String.format("SELECT %s FROM %s ", buildFieldsForSelect(), tableName);
		sqlFindByCustomQueryForCount = String.format("SELECT count(*) FROM %s ", tableName);
		sqlPartPaginator = " LIMIT :max OFFSET :offset";
	}

	protected String buildFieldsForSelect() {
		return "*";
	}

	protected SimpleJdbcInsert buildJdbcInsert() {
		SimpleJdbcInsert ret = new SimpleJdbcInsert(getDataSource()).withTableName(tableName);
		if (HasAutoincrementId.class.isAssignableFrom(dtoClass)) {
			ret = ret.usingGeneratedKeyColumns("id");
		}
		return ret;
	}

	protected SimpleJdbcUpdate initJdbcUpdate() {
		SimpleJdbcUpdate ret = new SimpleJdbcUpdate(getDataSource()).withTableName(tableName);

		// Configure identification columns - how do we find right record for
		// update
		List<String> restrictingColumns = new ArrayList<String>();
		restrictingColumns.add(QueryToNativeSqlCompilerMySqlImpl.underscore(HasId.FN_ID));
		if (HasTimestamps.class.isAssignableFrom(dtoClass)) {
			restrictingColumns.add(QueryToNativeSqlCompilerMySqlImpl.underscore(HasTimestamps.FN_MODIFIED_AT));
		}
		ret.setRestrictingColumns(restrictingColumns);

		ret.setUpdateColumnsEnlisterStrategy(updateColumnsEnlisterStrategy);
		return ret;
	}

	protected UpdateColumnsEnlisterStrategy updateColumnsEnlisterStrategy = new UpdateColumnsEnlisterStrategy() {
		@Override
		public Collection<? extends String> getColumnsForUpdate(TableMetaDataContext tableMetaDataContext) {
			List<String> updatingColumns = new ArrayList<String>(tableMetaDataContext.createColumns());
			remove("id", updatingColumns);
			if (HasTimestamps.class.isAssignableFrom(dtoClass)) {
				remove(QueryToNativeSqlCompilerMySqlImpl.underscore(HasTimestamps.FN_CREATED_AT), updatingColumns);
			}
			if (HasAuthor.class.isAssignableFrom(dtoClass)) {
				remove(QueryToNativeSqlCompilerMySqlImpl.underscore(HasAuthor.FN_CREATED_BY), updatingColumns);
			}
			return updatingColumns;
		}

		private void remove(String str, Iterable<String> iterable) {
			for (Iterator<String> iter = iterable.iterator(); iter.hasNext();) {
				if (str.equalsIgnoreCase(iter.next())) {
					iter.remove();
				}
			}
		}
	};

	@Override
	public void create(TDto dto) throws FieldValidationException {
		if (dto instanceof HasUuid) {
			HasUuid hasUuid = (HasUuid) dto;
			if (!stringIdGenerator.isValidId(hasUuid.getId())) {
				hasUuid.setId(stringIdGenerator.generateNewId(dto));
			}
		}

		if (dto instanceof HasTimestamps) {
			HasTimestamps hasTimestamps = (HasTimestamps) dto;
			long now = new Date().getTime();
			hasTimestamps.setCreatedAt(now);
			hasTimestamps.setModifiedAt(now);
		}

		SqlParameterSource params = parameterSourceBuilder.buildParameterSource(dto);
		try {
			if (dto instanceof HasAutoincrementId) {
				Number id = jdbcInsert.executeAndReturnKey(params);
				((HasAutoincrementId) dto).setId(id.longValue());
			} else {
				jdbcInsert.execute(params);
			}
		} catch (Throwable t) {
			daoExceptionToFveTranslator.translateAndThtowIfApplicable(t);
			throw t;
		}
	}

	@Override
	public int update(TDto dto) throws FieldValidationException {
		MapSqlParameterSource restrictionParams = new MapSqlParameterSource();
		restrictionParams.addValue(HasId.FN_ID, dto.getId());
		if (dto instanceof HasTimestamps) {
			HasTimestamps hasTimestamps = (HasTimestamps) dto;
			long modifiedAt = hasTimestamps.getModifiedAt();
			hasTimestamps.setModifiedAt(new Date().getTime());
			restrictionParams.addValue(HasTimestamps.FN_MODIFIED_AT, modifiedAt);
		}

		SqlParameterSource dtoParams = parameterSourceBuilder.buildParameterSource(dto);

		try {
			return jdbcUpdate.execute(dtoParams, restrictionParams);
		} catch (Throwable t) {
			daoExceptionToFveTranslator.translateAndThtowIfApplicable(t);
			throw t;
		}
	}

	@Override
	public TDto findById(TId id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);

		try {
			return jdbc.queryForObject(sqlFindById, params, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int delete(TId id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		return jdbc.update(sqlDeleteById, params);
	}

	@Override
	public int delete(TId id, long modifiedAt) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		params.addValue("modifiedAt", modifiedAt);
		return jdbc.update(sqlDeleteOptimisticById, params);
	}

	@Override
	public TDto findOneByQuery(Query query) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String whereClause = queryToNativeSqlCompiler.buildWhereClauseAndPopulateParams(query, params);

		try {
			return jdbc.queryForObject(sqlFindByCustomQuery + "WHERE " + whereClause, params, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int deleteByQuery(Query query) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String whereClause = queryToNativeSqlCompiler.buildWhereClauseAndPopulateParams(query, params);
		return jdbc.update(sqlDeleteByCustomQuery + whereClause, params);
	}

	@Override
	public PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String whereClause = optionalQuery == null ? ""
				: "WHERE " + queryToNativeSqlCompiler.buildWhereClauseAndPopulateParams(optionalQuery, params);
		params.addValue("offset", pagerParams.getOffset());
		params.addValue("max", pagerParams.getMax());

		String query = sqlFindByCustomQuery + whereClause + buildOrderBySubclause(orderBy);
		if (!PagerParams.ALL.equals(pagerParams)) {
			query = query + sqlPartPaginator;
		}
		List<TDto> list = jdbc.query(query, params, rowMapper);

		int totalResultsCount;
		if (Top.is(pagerParams) || (PagerParams.ALL.equals(pagerParams)
				|| (pagerParams.getOffset() == 0 && list.size() < pagerParams.getMax()))) {
			totalResultsCount = list.size();
		} else {
			// TODO: For MySQL we can use combination of SQL_CALC_FOUND_ROWS and
			// FOUND_ROWS() to improve performance -- ubt this is MySQL specific
			// functionality
			totalResultsCount = jdbc.queryForInt(sqlFindByCustomQueryForCount + whereClause, params);
		}

		return new PaginatedList<TDto>(pagerParams, list, totalResultsCount);
	}

	protected String buildOrderBySubclause(OrderBy[] orderBy) {
		if (orderBy == null || orderBy.length == 0) {
			return "";
		}

		StringBuilder ret = new StringBuilder();
		ret.append(" ORDER BY ");
		for (int i = 0; i < orderBy.length; i++) {
			if (i > 0) {
				ret.append(", ");
			}

			OrderBy o = orderBy[i];
			ret.append(QueryToNativeSqlCompilerMySqlImpl.underscore(o.getFieldName()));
			ret.append(" ");
			Preconditions.checkArgument(allowedSortDirections.contains(o.getDirection().toLowerCase()),
					"OrderBy not allowed: " + o.getDirection());
			ret.append(o.getDirection());
		}
		return ret.toString();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Class<TDto> getDtoClass() {
		return dtoClass;
	}

	public void setDtoClass(Class<TDto> dtoClass) {
		this.dtoClass = dtoClass;
	}

	public RowMapper<TDto> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<TDto> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public ParameterSourceBuilder<TDto> getParameterSourceBuilder() {
		return parameterSourceBuilder;
	}

	public void setParameterSourceBuilder(ParameterSourceBuilder<TDto> parameterSourceBuilder) {
		this.parameterSourceBuilder = parameterSourceBuilder;
	}

	public QueryToNativeSqlCompiler getQueryToNativeSqlCompiler() {
		return queryToNativeSqlCompiler;
	}

	@Autowired(required = false)
	public void setQueryToNativeSqlCompiler(QueryToNativeSqlCompiler queryToNativeSqlCompiler) {
		this.queryToNativeSqlCompiler = queryToNativeSqlCompiler;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Autowired(required = false)
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public StringIdGenerator getStringIdGenerator() {
		return stringIdGenerator;
	}

	/**
	 * Set it only if you want to customize default behavior when DTO's has
	 * {@link HasUuid} interface. Default is {@link StringIdGeneratorUuidImpl} and
	 * if. This is NOT applicable for other cases.
	 */
	@Autowired(required = false)
	public void setStringIdGenerator(StringIdGenerator stringIdGenerator) {
		this.stringIdGenerator = stringIdGenerator;
	}

	public DaoExceptionToFveTranslator getDuplicateEntryExceptionHandler() {
		return daoExceptionToFveTranslator;
	}

	@Autowired(required = false)
	public void setDuplicateEntryExceptionHandler(DaoExceptionToFveTranslator daoExceptionToFveTranslator) {
		this.daoExceptionToFveTranslator = daoExceptionToFveTranslator;
	}
}
