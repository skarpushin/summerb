package org.summerb.approaches.jdbccrud.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;

public class MultipleItemsResult<TId, TDto extends HasId<TId>> extends CrudQueryResult<TId, TDto> {
	private List<TDto> rows;
	private PagerParams pagerParams;
	private long totalResults;

	/**
	 * RowId -> [permissionName -> isAllowed]
	 */
	private Map<TId, Map<String, Boolean>> rowPermissions;

	/**
	 * permissionName -> isAllowed
	 */
	private Map<String, Boolean> tablePermissions;

	public MultipleItemsResult() {
	}

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