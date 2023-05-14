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
package org.summerb.easycrud.rest.dto;

import java.util.Map;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.datapackage.DataSet;
import org.summerb.easycrud.api.dto.relations.Ref;

public class CrudQueryResult<TId, TDto extends HasId<TId>> {

  protected String entityMessageCode;
  private Map<String, Ref> refsResolved;
  /** Resolved references, if any */
  private DataSet refs;

  public CrudQueryResult() {
    super();
  }

  public DataSet getRefs() {
    return refs;
  }

  public void setRefs(DataSet referenced) {
    this.refs = referenced;
  }

  public String getEntityMessageCode() {
    return entityMessageCode;
  }

  public void setEntityMessageCode(String entityMessageCode) {
    this.entityMessageCode = entityMessageCode;
  }

  public Map<String, Ref> getRefsResolved() {
    return refsResolved;
  }

  public void setRefsResolved(Map<String, Ref> refsResolved) {
    this.refsResolved = refsResolved;
  }
}
