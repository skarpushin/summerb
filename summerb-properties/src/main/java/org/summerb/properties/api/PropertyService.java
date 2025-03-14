/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

public interface PropertyService {
  void putSubjectProperty(
      String appName, String domainName, String subjectId, String name, String value);

  void putSubjectProperties(
      String appName, String domainName, String subjectId, List<NamedProperty> namedProperties);

  void putSubjectsProperty(
      String appName, String domainName, List<String> subjectsIds, String name, String value);

  String findSubjectProperty(String appName, String domainName, String subjectId, String name);

  /**
   * @return property name to property value map
   */
  Map<String, String> findSubjectProperties(String appName, String domainName, String subjectId);

  /**
   * @return subject id to (property name to property value) map
   */
  Map<String, Map<String, String>> findSubjectsProperties(
      String appName, String domainName, List<String> subjectsIds);

  /**
   * Convenience method to get value of same property for several subjects
   *
   * @return subject id to property value map
   */
  Map<String, String> findSubjectsProperty(
      String appName, String domainName, List<String> subjectsIds, String name);

  void deleteSubjectProperties(String appName, String domainName, String subjectId);

  void deleteSubjectsProperties(String appName, String domainName, List<String> subjectsIds);
}
