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
package org.summerb.users.api.dto;

import java.io.Serializable;

public class AuthToken implements Serializable {
  private static final long serialVersionUID = -1633738725204366881L;

  /** Token identifier 1st part, never changes */
  private String uuid;

  /** Token identifier 2nd part, expected to change after every token positive usage */
  private String tokenValue;

  private String userUuid;
  private long createdAt;
  private long expiresAt;

  /** When token was last verified. Expected to change every token usage */
  private long lastVerifiedAt;

  private String clientIp;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(long expiresAt) {
    this.expiresAt = expiresAt;
  }

  public long getLastVerifiedAt() {
    return lastVerifiedAt;
  }

  public void setLastVerifiedAt(long lastVerifiedAt) {
    this.lastVerifiedAt = lastVerifiedAt;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String getTokenValue() {
    return tokenValue;
  }

  public void setTokenValue(String tokenValue) {
    this.tokenValue = tokenValue;
  }

  @Override
  public String toString() {
    return "AuthToken [uuid="
        + uuid
        + ", tokenValue="
        + tokenValue
        + ", userUuid="
        + userUuid
        + ", createdAt="
        + createdAt
        + ", expiresAt="
        + expiresAt
        + ", lastVerifiedAt="
        + lastVerifiedAt
        + ", clientIp="
        + clientIp
        + "]";
  }
}
