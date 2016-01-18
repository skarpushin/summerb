package org.summerb.microservices.properties.impl.dto;

public class NamedIdProperty {
	private long nameId;
	private String propertyValue;

	public NamedIdProperty(long nameId, String propertyValue) {
		this.propertyValue = propertyValue;
		this.setNameId(nameId);
	}

	public long getNameId() {
		return nameId;
	}

	public void setNameId(long nameId) {
		this.nameId = nameId;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}
