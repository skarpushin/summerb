package org.summerb.approaches.jdbccrud.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.summerb.approaches.jdbccrud.api.ParameterSourceBuilder;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class ParameterSourceBuilderBeanPropImpl<TDto> implements ParameterSourceBuilder<TDto> {
	@Override
	public SqlParameterSource buildParameterSource(TDto dto) {
		return new BeanPropertySqlParameterSource(dto);
	}
}
