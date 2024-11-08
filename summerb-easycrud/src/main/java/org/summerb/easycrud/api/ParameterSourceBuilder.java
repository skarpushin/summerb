/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.summerb.easycrud.impl.dao.ParameterSourceBuilderBeanPropImpl;

/**
 * Strategy used to build parameter source when creating or updating row using {@link JdbcTemplate}.
 *
 * <p>Normally {@link ParameterSourceBuilderBeanPropImpl} is used but you can provide your own if
 * needed
 *
 * @author sergey.karpushin
 */
public interface ParameterSourceBuilder<TDto> {
  SqlParameterSource buildParameterSource(TDto dto);
}
