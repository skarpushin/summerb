/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.easycrud.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.summerb.easycrud.api.row.HasId;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

public class MultipleItemsResult<TId, TDto extends HasId<TId>> extends CrudQueryResult<TId, TDto> {
  protected List<TDto> rows;
  protected PagerParams pagerParams;
  protected long totalResults;

  /** RowId to [permissionName to isAllowed] */
  protected Map<TId, Map<String, Boolean>> rowPermissions;

  /** permissionName to isAllowed */
  protected Map<String, Boolean> tablePermissions;

  public MultipleItemsResult() {}

  public MultipleItemsResult(String entityMessageCode, PaginatedList<TDto> list) {
    this.entityMessageCode = entityMessageCode;
    this.pagerParams = list.getPagerParams();
    this.totalResults = list.getTotalResults();
    this.rows = new ArrayList<>(list.getItems());
  }

  public Map<TId, Map<String, Boolean>> getRowPermissions() {
    return rowPermissions;
  }

  public void setRowPermissions(Map<TId, Map<String, Boolean>> permissions) {
    this.rowPermissions = permissions;
  }

  public PagerParams getPagerParams() {
    return pagerParams;
  }

  public void setPagerParams(PagerParams pagerParams) {
    this.pagerParams = pagerParams;
  }

  public long getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(long totalResults) {
    this.totalResults = totalResults;
  }

  public Map<String, Boolean> getTablePermissions() {
    return tablePermissions;
  }

  public void setTablePermissions(Map<String, Boolean> tablePermissions) {
    this.tablePermissions = tablePermissions;
  }

  public List<TDto> getRows() {
    return rows;
  }

  public void setRows(List<TDto> rows) {
    this.rows = rows;
  }
}
