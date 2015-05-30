package org.summerb.easycrud.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.summerb.easycrud.api.ParameterSourceBuilder;

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
