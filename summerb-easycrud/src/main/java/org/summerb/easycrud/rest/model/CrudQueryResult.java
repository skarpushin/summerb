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
package org.summerb.easycrud.rest.model;

import org.summerb.easycrud.row.HasId;

/**
 * Result of a CRUD query.
 *
 * @param <TId> type of row ID
 * @param <TRow> type of row
 */
public class CrudQueryResult<TId extends Comparable<TId>, TRow extends HasId<TId>> {
  /** Message code for the entity */
  protected String entityMessageCode;

  /**
   * @return message code for the entity
   */
  public String getEntityMessageCode() {
    return entityMessageCode;
  }

  /**
   * @param entityMessageCode message code for the entity
   */
  public void setEntityMessageCode(String entityMessageCode) {
    this.entityMessageCode = entityMessageCode;
  }
}
