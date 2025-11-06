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

public class CommentRow implements HasAutoincrementId, HasAuthor, HasTimestamps, Serializable {
  @Serial private static final long serialVersionUID = 4373061148623858158L;

  private Long id;

  @ReferringTo(PostRow.class)
  private long postId;

  @ReferringTo(UserRow.class)
  private String authorId;

  private String comment;

  private long createdAt;
  private long modifiedAt;
  private String createdBy;
  private String modifiedBy;

  public CommentRow() {}

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public long getPostId() {
    return postId;
  }

  public void setPostId(long linkToDto2) {
    this.postId = linkToDto2;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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
  public String getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
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
  public String getModifiedBy() {
    return modifiedBy;
  }

  @Override
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }
}
