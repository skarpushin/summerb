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
package org.summerb.easycrud.rest.querynarrower;

import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

public class QueryNarrowerByCommonPathVariable<TRow> extends QueryNarrowerStrategyFieldBased<TRow> {

  public QueryNarrowerByCommonPathVariable(Class<TRow> rowClazz, String commonParamName) {
    super(rowClazz, commonParamName);
  }

  @Override
  protected Query<TRow> doNarrow(Query<TRow> ret, PathVariablesMap allRequestParams) {
    ret.eq(fieldName, allRequestParams.get(fieldName));
    return ret;
  }
}
