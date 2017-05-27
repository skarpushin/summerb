package org.summerb.approaches.jdbccrud.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.ParameterSourceBuilder;
import org.summerb.approaches.jdbccrud.api.QueryToNativeSqlCompiler;
import org.summerb.approaches.jdbccrud.api.dto.HasAuthor;
import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.HasTimestamps;
import org.summerb.approaches.jdbccrud.api.dto.HasUuid;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.dto.Top;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.common.DaoBase;
import org.summerb.approaches.jdbccrud.common.DaoExceptionUtils;
import org.summerb.approaches.jdbccrud.impl.SimpleJdbcUpdate.SimpleJdbcUpdate;
import org.summerb.approaches.jdbccrud.impl.SimpleJdbcUpdate.TableMetaDataContext;
import org.summerb.approaches.jdbccrud.impl.SimpleJdbcUpdate.UpdateColumnsEnlisterStrategy;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationUtils;
import org.summerb.approaches.validation.errors.DuplicateRecordValidationError;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class EasyCrudDaoMySqlImpl<TId, TDto extends HasId<TId>> extends DaoBase
		implements EasyCrudDao<TId, TDto>, InitializingBean {
	private static final List<String> allowedSortDirections = Arrays.asList("asc", "desc");

	private String tableName;
	private Class<TDto> dtoClass;
	private RowMapper<TDto> rowMapper;
	private ParameterSourceBuilder<TDto> parameterSourceBuilder;
	private QueryToNativeSqlCompiler queryToNativeSqlCompiler;

	private SimpleJdbcInsert jdbcInsert;
	private SimpleJdbcUpdate jdbcUpdate;

	private String sqlFindById;
	private String sqlDeleteById;
	private String sqlDeleteOptimisticById;
	private String sqlFindByCustomQuery;
	private String sqlDeleteByCustomQuery;
	private String sqlFindByCustomQueryForCount;
	private String sqlPartPaginator;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(StringUtils.hasText(tableName), "TableName required");
		Preconditions.checkState(dtoClass != null, "DtoClass required");

		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<TDto>(dtoClass);
		}

		jdbcInsert = buildJdbcInsert();
		jdbcUpdate = initJdbcUpdate();

		if (parameterSourceBuilder == null) {
			parameterSourceBuilder = new ParameterSourceBuilderBeanPropImpl<TDto>();
		}

		if (queryToNativeSqlCompiler == null) {
			queryToNativeSqlCompiler = new QueryToNativeSqlCompilerMySqlImpl();
		}

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

	private SimpleJdbcUpdate initJdbcUpdate() {
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

	private UpdateColumnsEnlisterStrategy updateColumnsEnlisterStrategy = new UpdateColumnsEnlisterStrategy() {
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
			if (!ValidationUtils.isValidNotNullableUuid(hasUuid.getId())) {
				hasUuid.setId(UUID.randomUUID().toString());
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
			processDuplicateEntryException(t);
			Throwables.propagate(t);
		}
	}

	private void processDuplicateEntryException(Throwable t) throws FieldValidationException {
		DuplicateKeyException dke = ExceptionUtils.findExceptionOfType(t, DuplicateKeyException.class);
		if (dke == null) {
			return;
		}

		String constraint = DaoExceptionUtils.findViolatedConstraintName(dke);
		// Handle case when uuid is duplicated
		if (DaoExceptionUtils.MYSQL_CONSTRAINT_PRIMARY.equals(constraint)) {
			throw new IllegalArgumentException("Row with same primary key already exists", dke);
		}

		if (!constraint.contains(DaoExceptionUtils.MYSQL_CONSTRAINT_UNIQUE)) {
			throw new IllegalArgumentException("Constraint violation " + constraint, dke);
		}

		String fieldName = constraint.substring(0, constraint.indexOf(DaoExceptionUtils.MYSQL_CONSTRAINT_UNIQUE));
		if (fieldName.contains("_")) {
			fieldName = JdbcUtils.convertUnderscoreNameToPropertyName(fieldName);
		}

		throw new FieldValidationException(new DuplicateRecordValidationError(fieldName));
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
			processDuplicateEntryException(t);
			Throwables.propagate(t);
			return Integer.MIN_VALUE; // never happens actually
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
			totalResultsCount = jdbc.queryForInt(sqlFindByCustomQueryForCount + whereClause, params);
		}

		return new PaginatedList<TDto>(pagerParams, list, totalResultsCount);
	}

	private String buildOrderBySubclause(OrderBy[] orderBy) {
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

	@Required
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

	public void setQueryToNativeSqlCompiler(QueryToNativeSqlCompiler queryToNativeSqlCompiler) {
		this.queryToNativeSqlCompiler = queryToNativeSqlCompiler;
	}

}
