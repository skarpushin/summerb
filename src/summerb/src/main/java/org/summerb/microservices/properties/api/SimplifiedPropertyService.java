package org.summerb.microservices.properties.api;

import java.util.List;
import java.util.Map;

import org.summerb.microservices.properties.api.dto.NamedProperty;

/**
 * Simplified version of {@link PropertyService}, will not require to pass
 * appName and domainName
 * 
 * @author skarpushin
 * 
 */
public interface SimplifiedPropertyService {
	void putSubjectProperty(String subjectId, String propertyName, String propertyValue);

	void putSubjectProperties(String subjectId, List<NamedProperty> namedProperties);

	void putSubjectsProperty(List<String> subjectsIds, String name, String value);

	String findSubjectProperty(String subjectId, String propertyName);

	/**
	 * @return property name to property value map
	 */
	Map<String, String> findSubjectProperties(String subjectId);

	/**
	 * @return subject id to (property name to property value) map
	 */
	Map<String, Map<String, String>> findSubjectsProperties(List<String> subjectsIds);

	/**
	 * Convenience method to get value of same property for several subjects
	 * 
	 * @return subject id to property value map
	 */
	Map<String, String> findSubjectsProperty(List<String> subjectsIds, String propertyName);

	/**
	 * Delete subject properties if any (will not fail if there is no
	 * proeprties)
	 * 
	 * @param subjectId
	 */
	void deleteSubjectProperties(String subjectId);

	void deleteSubjectsProperties(List<String> subjectsIds);
}
