package org.summerb.microservices.properties.api;

import java.util.List;
import java.util.Map;

import org.summerb.microservices.properties.api.dto.NamedProperty;

public interface PropertyService {
	void putSubjectProperty(String appName, String domainName, String subjectId, String name, String value);

	void putSubjectProperties(String appName, String domainName, String subjectId, List<NamedProperty> namedProperties);

	void putSubjectsProperty(String appName, String domainName, List<String> subjectsIds, String name, String value);

	String findSubjectProperty(String appName, String domainName, String subjectId, String name);

	/**
	 * @return property name to property value map
	 */
	Map<String, String> findSubjectProperties(String appName, String domainName, String subjectId);

	/**
	 * @return subject id to (property name to property value) map
	 */
	Map<String, Map<String, String>> findSubjectsProperties(String appName, String domainName,
			List<String> subjectsIds);

	/**
	 * Convenience method to get value of same property for several subjects
	 * 
	 * @return subject id to property value map
	 */
	Map<String, String> findSubjectsProperty(String appName, String domainName, List<String> subjectsIds, String name);

	void deleteSubjectProperties(String appName, String domainName, String subjectId);

	void deleteSubjectsProperties(String appName, String domainName, List<String> subjectsIds);
}
