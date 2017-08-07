package org.summerb.approaches.jdbccrud.rest.dto;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;

public class CrudQueryResult<TId, TDto extends HasId<TId>> {

	protected String entityMessageCode;
	private Map<String, Ref> refsResolved;
	/**
	 * Resolved references, if any
	 */
	private DataSet refs;

	public CrudQueryResult() {
		super();
	}

	public DataSet getRefs() {
		return refs;
	}

	public void setRefs(DataSet referenced) {
		this.refs = referenced;
	}

	public String getEntityMessageCode() {
		return entityMessageCode;
	}

	public void setEntityMessageCode(String entityMessageCode) {
		this.entityMessageCode = entityMessageCode;
	}

	public Map<String, Ref> getRefsResolved() {
		return refsResolved;
	}

	public void setRefsResolved(Map<String, Ref> refsResolved) {
		this.refsResolved = refsResolved;
	}

}