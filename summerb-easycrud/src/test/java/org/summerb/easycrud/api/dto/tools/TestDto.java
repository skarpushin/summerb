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
package org.summerb.easycrud.api.dto.tools;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.util.StringUtils;
import org.summerb.utils.DtoBase;

public class TestDto implements Serializable, DtoBase {
  @Serial private static final long serialVersionUID = 1404571618064571624L;

  private String uuid;
  private String displayName;
  private String email;
  private String timeZone;
  private String locale;
  private long registeredAt;
  private boolean isBlocked;
  private String integrationData;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public long getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(long registeredAt) {
    this.registeredAt = registeredAt;
  }

  public boolean getIsBlocked() {
    return isBlocked;
  }

  public void setIsBlocked(boolean isBlocked) {
    this.isBlocked = isBlocked;
  }

  public String getIntegrationData() {
    return integrationData;
  }

  public void setIntegrationData(String integrationData) {
    this.integrationData = integrationData;
  }

  @Override
  public String toString() {
    if (StringUtils.hasText(displayName)) {
      return displayName;
    }
    return "User [uuid="
        + uuid
        + ", displayName="
        + displayName
        + ", email="
        + email
        + ", timeZone="
        + timeZone
        + ", locale="
        + locale
        + "]";
  }
}
