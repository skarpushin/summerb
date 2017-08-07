package org.summerb.approaches.jdbccrud.common;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.google.common.base.Preconditions;

/**
 * This impl merges several sources to a single parameter source.
 * 
 * Comes in handy if needed to for most fields use
 * {@link BeanPropertySqlParameterSource} but use other parameter source for
 * just couple fields. Parameter sources invoked in the order they passed to
 * constructor
 * 
 * @author skarpushin
 * 
 */
public class SqlParameterSourceMergedImpl implements SqlParameterSource {
	private final SqlParameterSource[] parameterSources;

	public SqlParameterSourceMergedImpl(SqlParameterSource... parameterSources) {
		Preconditions.checkArgument(parameterSources != null);
		Preconditions.checkArgument(parameterSources.length > 0);
		this.parameterSources = parameterSources;
	}

	@Override
	public boolean hasValue(String paramName) {
		for (SqlParameterSource sqlParameterSource : parameterSources) {
			if (sqlParameterSource.hasValue(paramName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		IllegalArgumentException lastException = new IllegalArgumentException("No such parameter exists: " + paramName);

		for (SqlParameterSource sqlParameterSource : parameterSources) {
			try {
				return sqlParameterSource.getValue(paramName);
			} catch (IllegalArgumentException exc) {
				lastException = exc;
			}
		}

		throw lastException;
	}

	@Override
	public int getSqlType(String paramName) {
		for (SqlParameterSource sqlParameterSource : parameterSources) {
			int ret = sqlParameterSource.getSqlType(paramName);
			if (ret != TYPE_UNKNOWN) {
				return ret;
			}
		}
		return TYPE_UNKNOWN;
	}

	@Override
	public String getTypeName(String paramName) {
		for (SqlParameterSource sqlParameterSource : parameterSources) {
			String ret = sqlParameterSource.getTypeName(paramName);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

}
