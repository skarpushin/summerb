package org.summerb.approaches.jdbccrud.rest.dto;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;

public class SingleItemResult<TId, TDto extends HasId<TId>> extends CrudQueryResult<TId, TDto> {
	private TDto row;
	private Map<String, Boolean> permissions;

	public SingleItemResult() {
	}

	public SingleItemResult(String entityMessageCode, TDto row) {
		this.entityMessageCode = entityMessageCode;
		this.row = row;
	}

	public TDto getRow() {
		return row;
	}

	public void setRow(TDto row) {
		this.row = row;
	}

	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions = permissions;
	}

}