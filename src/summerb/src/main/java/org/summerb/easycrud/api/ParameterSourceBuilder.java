package org.summerb.easycrud.api;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface ParameterSourceBuilder<TDto> {
	SqlParameterSource buildParameterSource(TDto dto);
}