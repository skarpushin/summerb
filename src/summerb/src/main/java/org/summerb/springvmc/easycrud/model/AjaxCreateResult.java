package org.summerb.springvmc.easycrud.model;

public class AjaxCreateResult<T> {
	private T ajaxCreatedObj;

	public T getAjaxCreatedObj() {
		return ajaxCreatedObj;
	}

	public void setAjaxCreatedObj(T ajaxCreatedObj) {
		this.ajaxCreatedObj = ajaxCreatedObj;
	}
}
