package org.summerb.microservices.properties.impl.dao;

import java.util.List;

import org.summerb.microservices.properties.impl.dto.NamedIdProperty;

/**
 * DAO abstraction for proeprty store
 * 
 * @author skarpushin
 * 
 */
public interface PropertyDao {
	/**
	 * This constant used to identify data truncation errors for this specific
	 * situation when data truncation happened with property values.
	 * 
	 * IMPORTANT! If impl of this class will throw truncation errors they must
	 * use this name for value field
	 */
	public static final String VALUE_FIELD_NAME = "value";

	void putProperty(long appId, long domainId, String subjectId, long propertyNameId, String propertyValue);

	String findSubjectProperty(long appId, long domainId, String subjectId, long propertyNameId);

	List<NamedIdProperty> findSubjectProperties(long appId, long domainId, String subjectId);

	void deleteSubjectProperties(long appId, long domainId, String subjectId);

}
