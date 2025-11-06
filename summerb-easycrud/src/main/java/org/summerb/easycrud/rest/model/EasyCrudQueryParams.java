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

import java.util.Map;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.utils.easycrud.api.dto.PagerParams;

public class EasyCrudQueryParams {
  protected Map<String, FilteringParam> filterParams;
  protected OrderBy[] orderBy;
  protected PagerParams pagerParams;

  public PagerParams getPagerParams() {
    return pagerParams;
  }

  public void setPagerParams(PagerParams pagerParams) {
    this.pagerParams = pagerParams;
  }

  public Map<String, FilteringParam> getFilterParams() {
    return filterParams;
  }

  public void setFilterParams(Map<String, FilteringParam> filterParams) {
    this.filterParams = filterParams;
  }

  public OrderBy[] getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(OrderBy[] orderBy) {
    this.orderBy = orderBy;
  }
}
