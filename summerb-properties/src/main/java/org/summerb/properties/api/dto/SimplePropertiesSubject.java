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
package org.summerb.properties.api.dto;

import java.io.Serializable;
import org.summerb.utils.DtoBase;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;

/**
 * This DTO used for cache sync purposes for SimplePropertyService. Particularly this dto serves as
 * cache notification marker - if {@link EntityChangedEvent} with such value seen then it make sense
 * to invalidate cache with such key
 *
 * @author sergey.karpushin
 */
public class SimplePropertiesSubject implements Serializable, DtoBase {
  private static final long serialVersionUID = 7630700486193010855L;

  private String appName;
  private String domainName;
  private String subjectId;

  public SimplePropertiesSubject() {}

  public SimplePropertiesSubject(String appName, String domainName, String subjectId) {
    super();
    this.appName = appName;
    this.domainName = domainName;
    this.subjectId = subjectId;
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
}
