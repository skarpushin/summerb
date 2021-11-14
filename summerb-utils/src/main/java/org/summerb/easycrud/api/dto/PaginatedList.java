/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.easycrud.api.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * Paginated query results
 * 
 * @author sergey.karpushin
 *
 */
public class PaginatedList<T> implements Serializable {
	private static final long serialVersionUID = 1084861445691549809L;

	private List<T> items;
	private PagerParams pagerParams;
	private long totalResults;

	public PaginatedList() {

	}

	public PaginatedList(PagerParams pagerParams, List<T> items, long totalResults) {
		this.pagerParams = pagerParams;
		this.items = items;
		this.totalResults = totalResults;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
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

	public boolean getHasItems() {
		return !CollectionUtils.isEmpty(items);
	}

	@Override
	public String toString() {
		return "PaginatedList [pagerParams=" + pagerParams + ", totalResults=" + totalResults + ", items="
				+ (items == null ? "null" : "" + items.size()) + "]";
	}

}
