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

import org.springframework.beans.PropertyAccessor;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.Restriction;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

public abstract class QueryNarrowerStrategyFieldBased extends QueryNarrowerStrategy {
  protected String fieldName;

  public QueryNarrowerStrategyFieldBased(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public Query narrow(Query optionalQuery, PathVariablesMap pathVariables) {
    Query ret;
    if (optionalQuery == null) {
      ret = Query.n();
    } else {
      if (hasRestrictionOnField(optionalQuery, fieldName)) {
        return optionalQuery;
      }
      ret = optionalQuery;
    }

    ret = doNarrow(ret, pathVariables);

    return ret;
  }

  protected boolean hasRestrictionOnField(Query query, String fieldName) {
    for (Restriction<PropertyAccessor> restriction : query.getRestrictions()) {
      if (restriction instanceof FieldCondition) {
        if (fieldName.equals(((FieldCondition) restriction).getFieldName())) {
          return true;
        }
      } else if (restriction instanceof DisjunctionCondition) {
        for (Query subQuery : ((DisjunctionCondition) restriction).getQueries()) {
          if (hasRestrictionOnField(subQuery, fieldName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  protected abstract Query doNarrow(Query ret, PathVariablesMap allRequestParams);
}
