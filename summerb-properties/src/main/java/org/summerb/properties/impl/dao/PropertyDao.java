/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.properties.impl.dao;

import java.util.List;
import org.summerb.properties.impl.dto.NamedIdProperty;

/**
 * DAO abstraction for proeprty store
 *
 * @author skarpushin
 */
public interface PropertyDao {
  /**
   * This constant used to identify data truncation errors for this specific situation when data
   * truncation happened with property values.
   *
   * <p>IMPORTANT! If impl of this class will throw truncation errors they must use this name for
   * value field
   */
  String VALUE_FIELD_NAME = "value";

  void putProperty(
      long appId, long domainId, String subjectId, long propertyNameId, String propertyValue);

  String findSubjectProperty(long appId, long domainId, String subjectId, long propertyNameId);

  List<NamedIdProperty> findSubjectProperties(long appId, long domainId, String subjectId);

  void deleteSubjectProperties(long appId, long domainId, String subjectId);
}
