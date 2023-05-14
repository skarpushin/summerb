/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.properties.api;

import java.util.List;
import java.util.Map;

import org.summerb.properties.api.dto.NamedProperty;

/**
 * Simplified version of {@link PropertyService}, will not require to pass appName and domainName
 *
 * @author skarpushin
 */
public interface SimplifiedPropertyService {
  void putSubjectProperty(String subjectId, String propertyName, String propertyValue);

  void putSubjectProperties(String subjectId, List<NamedProperty> namedProperties);

  void putSubjectsProperty(List<String> subjectsIds, String name, String value);

  String findSubjectProperty(String subjectId, String propertyName);

  /** @return property name to property value map */
  Map<String, String> findSubjectProperties(String subjectId);

  /** @return subject id to (property name to property value) map */
  Map<String, Map<String, String>> findSubjectsProperties(List<String> subjectsIds);

  /**
   * Convenience method to get value of same property for several subjects
   *
   * @return subject id to property value map
   */
  Map<String, String> findSubjectsProperty(List<String> subjectsIds, String propertyName);

  /**
   * Delete subject properties if any (will not fail if there is no proeprties)
   *
   * @param subjectId
   */
  void deleteSubjectProperties(String subjectId);

  void deleteSubjectsProperties(List<String> subjectsIds);
}
