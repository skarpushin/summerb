package org.summerb.microservices.properties.api.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class NamedProperty implements Serializable {
	private static final long serialVersionUID = -4959586964104535553L;

	private String name;
	private String propertyValue;

	public NamedProperty() {

	}

	public NamedProperty(String name, String value) {
		this.name = name;
		this.propertyValue = value;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		if (StringUtils.hasText(name)) {
			ret.append(name);
		} else {
			ret.append("<Unnamed property>");
		}

		ret.append(" = ");

		if (propertyValue == null) {
			ret.append("<no value>");
		} else {
			ret.append("'");
			ret.append(propertyValue);
			ret.append("'");
		}

		return ret.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedProperty other = (NamedProperty) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (propertyValue == null) {
			if (other.propertyValue != null)
				return false;
		} else if (!propertyValue.equals(other.propertyValue))
			return false;
		return true;
	}
}
