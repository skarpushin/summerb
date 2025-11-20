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
package org.summerb.easycrud.sql_builder.mysql;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.dao.SqlTypeOverridesDefaultImpl;
import org.summerb.easycrud.query.Condition;
import org.summerb.easycrud.query.DisjunctionCondition;
import org.summerb.easycrud.query.FieldCondition;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.query.restrictions.Between;
import org.summerb.easycrud.query.restrictions.Empty;
import org.summerb.easycrud.query.restrictions.Equals;
import org.summerb.easycrud.query.restrictions.In;
import org.summerb.easycrud.query.restrictions.IsNull;
import org.summerb.easycrud.query.restrictions.Less;
import org.summerb.easycrud.query.restrictions.Like;
import org.summerb.easycrud.query.restrictions.StringLengthBetween;
import org.summerb.easycrud.query.restrictions.StringLengthLess;
import org.summerb.easycrud.query.restrictions.base.Restriction;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.impl.ParamIdxIncrementer;
import org.summerb.easycrud.sql_builder.mysql.restrictions.BetweenRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.EmptyRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.EqualsRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.InRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.IsNullRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.LessRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.LikeRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.RestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.StringLengthBetweenRestrictionToNativeSql;
import org.summerb.easycrud.sql_builder.mysql.restrictions.StringLengthLessRestrictionToNativeSql;

/**
 * MySQL specific impl of {@link QueryToSql}
 *
 * @author sergey.karpushin
 */
@SuppressWarnings("rawtypes")
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
  public String buildFilter(Query query, MapSqlParameterSource params) {
    StringBuilder sb = new StringBuilder();
    ParamIdxIncrementer paramIdx = new ParamIdxIncrementer();
    buildFilter(query, null, params, paramIdx, sb);
    return sb.toString();
  }

  @Override
  public void buildFilter(
      Query query,
      MapSqlParameterSource params,
      String alias,
      StringBuilder sql,
      ParamIdxIncrementer paramIdxIncrementer) {
    buildFilter(query, alias, params, paramIdxIncrementer, sql);
  }

  protected void buildFilter(
      Query query,
      String alias,
      MapSqlParameterSource params,
      Supplier<Integer> paramIdx,
      StringBuilder sb) {
    List<Condition> rr = query.getConditions();
    for (int i = 0; i < rr.size(); i++) {
      Condition r = rr.get(i);
      if (i > 0) {
        sb.append(" AND ");
      }

      if (r instanceof FieldCondition) {
        sb.append(buildCondition((FieldCondition) r, params, paramIdx, alias));
      } else if (r instanceof DisjunctionCondition dc) {
        sb.append("(");
        boolean added = false;
        for (int j = 0; j < dc.getQueries().size(); j++) {
          Query orQuery = (Query) dc.getQueries().get(j);
          if (orQuery.isGuaranteedToYieldEmptyResultset()) {
            continue;
          }
          if (added) {
            sb.append(" OR (");
          } else {
            sb.append("(");
          }
          buildFilter(orQuery, alias, params, paramIdx, sb);
          added = true;
          sb.append(")");
        }
        if (!added) {
          throw new IllegalStateException(
              "It shouldn't have come to a moment when OR clause with all Queries with isGuaranteedToYieldEmptyResultset() == true is being translated to SQL");
        }
        sb.append(")");
      } else {
        throw new IllegalStateException("Unsupported condition: " + r);
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected String buildCondition(
      FieldCondition c, MapSqlParameterSource params, Supplier<Integer> paramIdx, String alias) {
    String underscoredFieldName =
        alias == null ? snakeCase(c.getFieldName()) : alias + "." + snakeCase(c.getFieldName());

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

  public static String snakeCase(String name) {
    if (!StringUtils.hasLength(name)) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    result.append(Character.toLowerCase(name.charAt(0)));

    for (int i = 1; i < name.length(); i++) {
      char currentChar = name.charAt(i);
      char previousChar = name.charAt(i - 1);

      if (currentChar == '_') {
        // Preserve existing underscores
        result.append('_');
      } else if (Character.isUpperCase(currentChar)) {
        boolean shouldAddUnderscore = false;

        // Add underscore if previous char is lowercase (camelCase boundary)
        if (Character.isLowerCase(previousChar)) {
          shouldAddUnderscore = true;
        }
        // Add underscore if we're at the end of an acronym (current is upper, next is lower)
        else if (i < name.length() - 1 && Character.isLowerCase(name.charAt(i + 1))) {
          shouldAddUnderscore = true;
        }

        if (shouldAddUnderscore) {
          result.append('_');
        }
        result.append(Character.toLowerCase(currentChar));
      } else {
        result.append(currentChar);
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
}
