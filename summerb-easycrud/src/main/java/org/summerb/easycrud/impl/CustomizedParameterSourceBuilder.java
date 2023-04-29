package org.summerb.easycrud.impl;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Abstract class for customizing mapping of field to parameters when executing
 * DAO queries
 *
 * @author Sergey Karpushin
 */
public class CustomizedParameterSourceBuilder<T> extends ParameterSourceBuilderBeanPropImpl<T> {

	protected Map<String, FieldOverride<T>> overrides;

	public CustomizedParameterSourceBuilder(FieldOverride<T> override) {
		super();
		Preconditions.checkArgument(override != null, "override required");
		this.overrides = new HashMap<>();
		this.overrides.put(override.fieldName, override);
	}

	public CustomizedParameterSourceBuilder(Collection<FieldOverride<T>> overrides) {
		super();
		Preconditions.checkArgument(!CollectionUtils.isEmpty(overrides), "overrides required");
		this.overrides = overrides.stream().collect(Collectors.toMap(k -> k.fieldName, Function.identity()));
	}

	@Override
	public SqlParameterSource buildParameterSource(T dto) {
		return new BeanPropertySqlParameterSource(dto) {
			@Override
			public Object getValue(String paramName) throws IllegalArgumentException {
				FieldOverride<T> override = overrides.get(paramName);
				if (override == null) {
					return super.getValue(paramName);
				}

				return override.valueGetter.apply(dto);
			}

			@Override
			public int getSqlType(String paramName) {
				FieldOverride<T> override = overrides.get(paramName);
				if (override == null) {
					return super.getSqlType(paramName);
				}

				return override.sqlType;
			}
		};
	}

	public static class FieldOverride<T> {
		String fieldName;
		int sqlType;
		Function<T, Object> valueGetter;

		/**
		 * @param fieldName   field to override
		 * @param type        one of {@link Types}
		 * @param valueGetter function to get field value
		 */
		public static <T> FieldOverride<T> of(String fieldName, int type, Function<T, Object> valueGetter) {
			Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");
			Preconditions.checkArgument(valueGetter != null, "valueGetter required");
			FieldOverride<T> ret = new FieldOverride<>();
			ret.fieldName = fieldName;
			ret.sqlType = type;
			ret.valueGetter = valueGetter;
			return ret;
		}
	}
}
