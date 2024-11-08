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
package org.summerb.easycrud.api.row.relations;

import org.summerb.easycrud.api.row.HasAutoincrementId;
import org.summerb.utils.DtoBase;

/**
 * DTO used to describe m2m table. Suites only very simple cases. In case m2m table need to contain
 * custom fields it's better to construct your own DTO instead of trying to subclass this one.
 *
 * @author sergeyk
 * @param <T1Id> type of referencer id (who references)
 * @param <T2Id> type of referencee id (who is being referenced)
 */
public class ManyToManyRow<T1Id, T2Id> implements DtoBase, HasAutoincrementId {
  private static final long serialVersionUID = 2609297133758985L;

  public static final String FN_SRC = "src";
  public static final String FN_DST = "dst";

  protected Long id;
  protected T1Id src;
  protected T2Id dst;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public T1Id getSrc() {
    return src;
  }

  public void setSrc(T1Id a) {
    this.src = a;
  }

  public T2Id getDst() {
    return dst;
  }

  public void setDst(T2Id b) {
    this.dst = b;
  }
}
