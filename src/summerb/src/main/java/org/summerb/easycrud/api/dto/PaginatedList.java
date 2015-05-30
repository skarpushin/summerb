package org.summerb.easycrud.api.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * 
 * @author sergey.karpushin
 *
 */public class PaginatedList<T> implements Serializable {
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
