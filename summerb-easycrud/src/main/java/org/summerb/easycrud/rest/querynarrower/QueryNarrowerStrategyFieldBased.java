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
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.query.Condition;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

public abstract class QueryNarrowerStrategyFieldBased<TId, TRow extends HasId<TId>>
    extends QueryNarrowerStrategy<TId, TRow> {
  protected String fieldName;
  protected EasyCrudService<TId, TRow> service;

  public QueryNarrowerStrategyFieldBased(EasyCrudService<TId, TRow> service, String fieldName) {
    Preconditions.checkNotNull(service, "service required");
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");

    this.fieldName = fieldName;
    this.service = service;
  }

  @Override
  public Query<TId, TRow> narrow(Query<TId, TRow> optionalQuery, PathVariablesMap pathVariables) {
    Query<TId, TRow> ret;
    if (optionalQuery == null) {
      ret = service.query();
    } else {
      if (hasRestrictionOnField(optionalQuery, fieldName)) {
        return optionalQuery;
      }
      ret = optionalQuery;
    }

    ret = doNarrow(ret, pathVariables);

    return ret;
  }

  protected boolean hasRestrictionOnField(Query<TId, TRow> query, String fieldName) {
    for (Condition condition : query.getConditions()) {
      if (condition instanceof FieldCondition) {
        if (fieldName.equals(((FieldCondition) condition).getFieldName())) {
          return true;
        }
      } else if (condition instanceof DisjunctionCondition) {
        for (Query<TId, TRow> subQuery :
            ((DisjunctionCondition<TId, TRow>) condition).getQueries()) {
          if (hasRestrictionOnField(subQuery, fieldName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  protected abstract Query<TId, TRow> doNarrow(
      Query<TId, TRow> ret, PathVariablesMap allRequestParams);
}
