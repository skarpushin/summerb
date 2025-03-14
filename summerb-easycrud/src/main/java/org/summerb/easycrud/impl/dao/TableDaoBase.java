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
package org.summerb.easycrud.impl.dao;

import com.google.common.base.Preconditions;
import javax.sql.DataSource;
import org.springframework.util.StringUtils;

/**
 * Simple base class for all DAOs
 *
 * @author sergey.karpushin
 */
public abstract class TableDaoBase extends DaoBase {
  protected String tableName;

  /**
   * Constructor for cases when subclass wants to take full responsibility on instantiation process.
   *
   * @deprecated when using this constructor please make sure you're properly initializing required
   *     dependencies: {@link #dataSource} and {@link #tableName}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  protected TableDaoBase() {}

  protected TableDaoBase(DataSource dataSource, String tableName) {
    super(dataSource);
    this.tableName = tableName;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName required");
  }
}
