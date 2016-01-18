package org.summerb.approaches.jdbccrud.mvc.model;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.mvc.filter.FilteringParam;

public class EasyCrudQueryParams {
	private Map<String, FilteringParam> filterParams;
	private OrderBy[] orderBy;
	private PagerParams pagerParams;

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
