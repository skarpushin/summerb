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
package org.summerb.easycrud.impl.dao.mysql;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.query.Condition;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.api.query.restrictions.Between;
import org.summerb.easycrud.api.query.restrictions.Empty;
import org.summerb.easycrud.api.query.restrictions.Equals;
import org.summerb.easycrud.api.query.restrictions.In;
import org.summerb.easycrud.api.query.restrictions.IsNull;
import org.summerb.easycrud.api.query.restrictions.Less;
import org.summerb.easycrud.api.query.restrictions.Like;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetween;
import org.summerb.easycrud.api.query.restrictions.StringLengthLess;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;
import org.summerb.easycrud.impl.dao.SqlTypeOverridesDefaultImpl;
import org.summerb.easycrud.impl.dao.mysql.restrictions.BetweenRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.EmptyRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.EqualsRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.InRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.IsNullRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.LessRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.LikeRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.RestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.StringLengthBetweenRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.mysql.restrictions.StringLengthLessRestrictionToNativeSql;

/**
 * MySQL specific impl of {@link QueryToSql}
 *
 * @author sergey.karpushin
 */
public class QueryToSqlMySqlImpl implements QueryToSql {

  protected Map<Class<? extends Restriction>, RestrictionToNativeSql<? extends Restriction>>
      converters = new HashMap<>();

  protected SqlTypeOverrides sqlTypeOverrides = new SqlTypeOverridesDefaultImpl();

  public QueryToSqlMySqlImpl(SqlTypeOverrides sqlTypeOverrides) {
    this();
    Preconditions.checkNotNull(sqlTypeOverrides, "sqlTypeOverrides required");
    this.sqlTypeOverrides = sqlTypeOverrides;
  }

  public QueryToSqlMySqlImpl() {
    converters.put(Between.class, new BetweenRestrictionToNativeSql());
    converters.put(Empty.class, new EmptyRestrictionToNativeSql());
    converters.put(Equals.class, new EqualsRestrictionToNativeSql());
    converters.put(In.class, new InRestrictionToNativeSql());
    converters.put(IsNull.class, new IsNullRestrictionToNativeSql());
    converters.put(Less.class, new LessRestrictionToNativeSql());
    converters.put(Like.class, new LikeRestrictionToNativeSql());
    converters.put(StringLengthBetween.class, new StringLengthBetweenRestrictionToNativeSql());
    converters.put(StringLengthLess.class, new StringLengthLessRestrictionToNativeSql());
  }

  @Override
  public String buildWhereClauseAndPopulateParams(
      QueryConditions query, MapSqlParameterSource params) {
    StringBuilder sb = new StringBuilder();
    ParamIdxIncrementer paramIdx = new ParamIdxIncrementer();
    buildWhereClauseAndPopulateParams(query, params, paramIdx, sb);
    return sb.toString();
  }

  protected void buildWhereClauseAndPopulateParams(
      QueryConditions query,
      MapSqlParameterSource params,
      Supplier<Integer> paramIdx,
      StringBuilder sb) {
    sb.append("(");
    List<Condition> rr = query.getConditions();
    for (int i = 0; i < rr.size(); i++) {
      Condition r = rr.get(i);
      if (i > 0) {
        sb.append(" AND ");
      }

      if (r instanceof FieldCondition) {
        sb.append(buildCondition((FieldCondition) r, params, paramIdx));
      } else if (r instanceof DisjunctionCondition) {
        DisjunctionCondition dc = (DisjunctionCondition) r;
        sb.append("(");
        for (int j = 0; j < dc.getQueries().size(); j++) {
          if (j > 0) {
            sb.append(" OR ");
          }
          buildWhereClauseAndPopulateParams(dc.getQueries().get(j), params, paramIdx, sb);
        }
        sb.append(")");
      } else {
        throw new IllegalStateException("Unsupported condition: " + r);
      }
    }
    sb.append(")");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected String buildCondition(
      FieldCondition c, MapSqlParameterSource params, Supplier<Integer> paramIdx) {
    String underscoredFieldName = underscore(c.getFieldName());

    RestrictionToNativeSql converter = converters.get(c.getRestriction().getClass());
    if (converter == null) {
      throw new IllegalStateException("Unsupported restriction: " + c);
    }

    return converter.convert(
        c.getRestriction(), params, paramIdx, underscoredFieldName, sqlTypeOverrides);
  }

  public static String buildNextParamName(Supplier<Integer> nextParameterIndex) {
    return "arg" + nextParameterIndex.get();
  }

  public static String underscore(String name) {
    StringBuilder result = new StringBuilder();
    if (name != null && !name.isEmpty()) {
      result.append(name.substring(0, 1).toLowerCase());
      for (int i = 1; i < name.length(); i++) {
        String s = name.substring(i, i + 1);
        if (s.equals(s.toUpperCase())) {
          result.append("_");
          result.append(s.toLowerCase());
        } else {
          result.append(s);
        }
      }
    }
    return result.toString();
  }

  public SqlTypeOverrides getOverrides() {
    return sqlTypeOverrides;
  }

  public void setOverrides(SqlTypeOverrides overrides) {
    Preconditions.checkNotNull(overrides, "sqlTypeOverrides required");
    this.sqlTypeOverrides = overrides;
  }

  protected static class ParamIdxIncrementer implements Supplier<Integer> {
    protected int idx = 0;

    @Override
    public Integer get() {
      int ret = idx;
      idx++;
      return ret;
    }
  }
}
