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
package org.summerb.easycrud.impl.relations.example;

import java.io.Serial;
import org.summerb.easycrud.api.row.HasAuthor;
import org.summerb.easycrud.api.row.HasAutoincrementId;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.utils.DtoBase;

public abstract class RowBase implements DtoBase, HasAutoincrementId, HasTimestamps, HasAuthor {
  @Serial private static final long serialVersionUID = 801702724424959230L;

  private Long id;
  private long createdAt;
  private long modifiedAt;
  private String createdBy;
  private String modifiedBy;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
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
}
