package org.summerb.microservices.properties.api.dto;

import java.io.Serializable;

public class Property implements Serializable {
	private static final long serialVersionUID = -1313040843994092678L;

	private String appName;
	private String domainName;
	private String subjectId;
	private String propertyName;
	private String propertyValue;

	public Property() {
		// for IO purposes
	}

	public Property(String appName, String domainName, String subjectId, String propertyName, String propertyValue) {
		super();
		this.appName = appName;
		this.domainName = domainName;
		this.subjectId = subjectId;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
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

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
}
