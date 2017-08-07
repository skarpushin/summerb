package org.summerb.approaches.jdbccrud.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.summerb.approaches.jdbccrud.impl.ParameterSourceBuilderBeanPropImpl;

/**
 * Strategy used to build parameter source when creating or updating row using
 * {@link JdbcTemplate}.
 * 
 * Normally {@link ParameterSourceBuilderBeanPropImpl} is used but you can
 * provide your own if needed
 * 
 * @author sergey.karpushin
 *
 */
public interface ParameterSourceBuilder<TDto> {
	SqlParameterSource buildParameterSource(TDto dto);
}
