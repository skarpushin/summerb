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
package integr.org.summerb.easycrud.dtos;

import java.io.Serial;
import java.io.Serializable;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.row.HasAuthor;
import org.summerb.easycrud.row.HasAutoincrementId;
import org.summerb.easycrud.row.HasTimestamps;

public class PostRow implements HasAutoincrementId, HasAuthor, HasTimestamps, Serializable {
  @Serial private static final long serialVersionUID = -2954623750074589334L;

  private Long id;

  private String title;
  private String body;
  private int likes;
  private int dislikes;

  @ReferringTo(UserRow.class)
  private String authorId;

  @ReferringTo(UserRow.class)
  private String pinnedBy;

  private long createdAt;
  private long modifiedAt;

  @ReferringTo(UserRow.class)
  private String createdBy;

  @ReferringTo(UserRow.class)
  private String modifiedBy;

  public PostRow() {}

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public int getDislikes() {
    return dislikes;
  }

  public void setDislikes(int dislikes) {
    this.dislikes = dislikes;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getPinnedBy() {
    return pinnedBy;
  }

  public void setPinnedBy(String pinnedBy) {
    this.pinnedBy = pinnedBy;
  }
}
