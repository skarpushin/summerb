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
package org.summerb.easycrud.impl.dao;

import com.google.common.base.Preconditions;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;

/**
 * Simple base class for all DAOs
 *
 * @author sergey.karpushin
 */
public abstract class DaoBase implements InitializingBean {
  protected DataSource dataSource;
  protected NamedParameterJdbcTemplateEx jdbc;

  /**
   * Constructor for cases when sub-class wants to take full responsibility on instantiation
   * process.
   *
   * @deprecated when using this constructor please make sure you're properly initializing required
   *     dependencies: {@link #dataSource}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  public DaoBase() {}

  public DaoBase(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Preconditions.checkArgument(dataSource != null, "dataSource required");
    this.jdbc = new NamedParameterJdbcTemplateEx(dataSource);
  }
}
