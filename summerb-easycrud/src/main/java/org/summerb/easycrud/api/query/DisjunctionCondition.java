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
package org.summerb.easycrud.api.query;

import java.util.Arrays;

import org.springframework.beans.PropertyAccessor;

/**
 * By default {@link Query} adds all restrictions in conjunction. In order to use disjunction add
 * instance of this {@link DisjunctionCondition} to root {@link Query}
 *
 * @author sergey.karpushin
 */
public class DisjunctionCondition implements Restriction<PropertyAccessor> {
  private static final long serialVersionUID = -8190060986073922292L;
  private Query[] queries;

  public DisjunctionCondition() {}

  public DisjunctionCondition(Query... queries) {
    this.queries = queries;
  }

  @Override
  public boolean isMeet(PropertyAccessor subjectValue) {
    for (int i = 0; i < queries.length; i++) {
      if (queries[i].isMeet(subjectValue)) {
        return true;
      }
    }
    return false;
  }

  public Query[] getQueries() {
    return queries;
  }

  public void setQueries(Query[] queries) {
    this.queries = queries;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(queries);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DisjunctionCondition other = (DisjunctionCondition) obj;
    if (!Arrays.equals(queries, other.queries)) return false;
    return true;
  }
}
