package org.summerb.microservices.properties.api;

import java.util.List;
import java.util.Map;

import org.summerb.microservices.properties.api.dto.NamedProperty;

/**
 * Very simple property service easily suitable for caching wrapper
 * 
 * @author sergeyk
 *
 */
public interface SimplePropertyService {
	Map<String, String> findSubjectProperties(String subjectId);

	String findSubjectProperty(String subjectId, String propertyName);

	void putSubjectProperties(String subjectId, List<NamedProperty> namedProperties);

	void putSubjectProperty(String subjectId, String propertyName, String propertyValue);

	void deleteSubjectProperties(String subjectId);

	String getAppName();

	String getDomainName();
}
