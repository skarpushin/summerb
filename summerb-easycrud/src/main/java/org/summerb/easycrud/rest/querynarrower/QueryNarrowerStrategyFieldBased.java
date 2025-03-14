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
package org.summerb.easycrud.rest.querynarrower;

import com.google.common.base.Preconditions;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.query.Condition;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

public abstract class QueryNarrowerStrategyFieldBased<TRow> extends QueryNarrowerStrategy<TRow> {
  protected String fieldName;
  protected Class<TRow> rowClazz;

  public QueryNarrowerStrategyFieldBased(Class<TRow> rowClazz, String fieldName) {
    Preconditions.checkArgument(rowClazz != null, "rowClazz required");
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");

    this.fieldName = fieldName;
    this.rowClazz = rowClazz;
  }

  @Override
  public Query<TRow> narrow(Query<TRow> optionalQuery, PathVariablesMap pathVariables) {
    Query<TRow> ret;
    if (optionalQuery == null) {
      ret = Query.n(rowClazz);
    } else {
      if (hasRestrictionOnField(optionalQuery, fieldName)) {
        return optionalQuery;
      }
      ret = optionalQuery;
    }

    ret = doNarrow(ret, pathVariables);

    return ret;
  }

  protected boolean hasRestrictionOnField(QueryConditions query, String fieldName) {
    for (Condition condition : query.getConditions()) {
      if (condition instanceof FieldCondition) {
        if (fieldName.equals(((FieldCondition) condition).getFieldName())) {
          return true;
        }
      } else if (condition instanceof DisjunctionCondition) {
        for (QueryConditions subQuery : ((DisjunctionCondition) condition).getQueries()) {
          if (hasRestrictionOnField(subQuery, fieldName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  protected abstract Query<TRow> doNarrow(Query<TRow> ret, PathVariablesMap allRequestParams);
}
