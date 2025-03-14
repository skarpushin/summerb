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
package org.summerb.easycrud.scaffold.impl;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoInjections;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;

/**
 * Impl of {@link ScaffoldedMethodFactory} assuming underlying DB is MySQL (but could as well work
 * for other DBs too)
 *
 * @author sergeyk
 */
public class ScaffoldedMethodFactoryMySqlImpl implements ScaffoldedMethodFactory {
  protected static final Logger log =
      LoggerFactory.getLogger(ScaffoldedMethodFactoryMySqlImpl.class);
  protected static final MapSqlParameterSource EMPTY_PARAMETER_SOURCE = new MapSqlParameterSource();

  public ScaffoldedMethodFactoryMySqlImpl() {}

  @Override
  public CallableMethod create(
      EasyCrudService<?, HasId<?>> service, EasyCrudDaoInjections<?, HasId<?>> dao, Method method) {
    return new ScaffoldedQueryMethodImpl<>(service, dao, method);
  }
}
