package org.summerb.microservices.properties.api.dto;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;

/**
 * This DTO used for cache sync purposes for SimplePropertyService. Particularly
 * this dto serves as cache notification marker - if {@link EntityChangedEvent}
 * with such value seen then it make sense to invalidate cache with such key
 * 
 * @author sergey.karpushin
 *
 */
public class SimplePropertiesSubject implements Serializable {
	private static final long serialVersionUID = 7630700486193010855L;

	private String appName;
	private String domainName;
	private String subjectId;

	public SimplePropertiesSubject() {
	}

	public SimplePropertiesSubject(String appName, String domainName, String subjectId) {
		super();
		this.appName = appName;
		this.domainName = domainName;
		this.subjectId = subjectId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
}
