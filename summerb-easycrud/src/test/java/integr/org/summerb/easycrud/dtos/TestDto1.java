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
package integr.org.summerb.easycrud.dtos;

import java.io.Serializable;

import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasAuthor;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.easycrud.api.row.HasUuid;

public class TestDto1 implements HasUuid, HasAuthor, HasTimestamps, Serializable {
  private static final long serialVersionUID = -2954623750074589334L;

  /** @return shortcut for building {@link Query} for this DTO */
  public static Query<TestDto1> Q() {
    return Query.FACTORY.buildFor(TestDto1.class);
  }

  private String id;
  private String env;
  private boolean active;
  private int majorVersion;
  private int minorVersion;
  private long createdAt;
  private long modifiedAt;
  private String createdBy;
  private String modifiedBy;
  private String linkToFullDonwload;
  private String linkToPatchToNextVersion;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getEnv() {
    return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public void setMajorVersion(int majorVersion) {
    this.majorVersion = majorVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public void setMinorVersion(int minorVersion) {
    this.minorVersion = minorVersion;
  }

  @Override
  public long getCreatedAt() {
    return createdAt;
  }

  @Override
  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public long getModifiedAt() {
    return modifiedAt;
  }

  @Override
  public void setModifiedAt(long modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  @Override
  public String getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public String getModifiedBy() {
    return modifiedBy;
  }

  @Override
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getLinkToFullDonwload() {
    return linkToFullDonwload;
  }

  public void setLinkToFullDonwload(String linkToFullDonwload) {
    this.linkToFullDonwload = linkToFullDonwload;
  }

  public String getLinkToPatchToNextVersion() {
    return linkToPatchToNextVersion;
  }

  public void setLinkToPatchToNextVersion(String linkToPatchToNextVersion) {
    this.linkToPatchToNextVersion = linkToPatchToNextVersion;
  }
}
