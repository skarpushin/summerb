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
package org.summerb.easycrud.impl.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.PropertyAccessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.QueryToNativeSqlCompiler;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.Restriction;
import org.summerb.easycrud.api.query.restrictions.BooleanEqRestriction;
import org.summerb.easycrud.api.query.restrictions.IsNullRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberEqRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberGreaterOrEqualRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberLessOrEqualsRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringEqRestriction;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringLessOrEqualsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringStartsWithRestriction;

/**
 * MySQL specific impl of {@link QueryToNativeSqlCompiler}
 *
 * @author sergey.karpushin
 */
public class QueryToNativeSqlCompilerMySqlImpl implements QueryToNativeSqlCompiler {

  protected Map<Class<? extends Restriction<?>>, ConditionConverter<? extends Restriction<?>>>
      converters = new HashMap<>();

  public QueryToNativeSqlCompilerMySqlImpl() {
    converters.put(BooleanEqRestriction.class, booleanEqRestriction);
    converters.put(IsNullRestriction.class, isNullRestriction);

    converters.put(NumberBetweenRestriction.class, numberBetweenRestriction);
    converters.put(NumberEqRestriction.class, numberEqRestriction);
    converters.put(NumberLessOrEqualsRestriction.class, numberLessOrEqualsRestriction);
    converters.put(NumberGreaterOrEqualRestriction.class, numberGreaterOrEqualRestriction);
    converters.put(NumberOneOfRestriction.class, numberOneOfRestriction);

    converters.put(StringEqRestriction.class, stringEqRestriction);
    converters.put(StringOneOfRestriction.class, stringOneOfRestriction);
    converters.put(StringContainsRestriction.class, stringContainsRestriction);
    converters.put(StringBetweenRestriction.class, stringBetweenRestriction);
    converters.put(StringLengthBetweenRestriction.class, stringLengthBetweenRestriction);
    converters.put(StringStartsWithRestriction.class, stringStartsWithRestriction);
    converters.put(StringLessOrEqualsRestriction.class, stringLessOrEqualsRestriction);
  }

  @Override
  public String buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params) {
    StringBuilder sb = new StringBuilder();
    AtomicInteger paramIdx = new AtomicInteger(0);
    buildWhereClauseAndPopulateParams(query, params, paramIdx, sb);
    return sb.toString();
  }

  protected void buildWhereClauseAndPopulateParams(
      Query query, MapSqlParameterSource params, AtomicInteger paramIdx, StringBuilder sb) {
    sb.append("(");
    List<Restriction<PropertyAccessor>> rr = query.getRestrictions();
    for (int i = 0; i < rr.size(); i++) {
      Restriction<PropertyAccessor> r = rr.get(i);
      if (i > 0) {
        sb.append(" AND ");
      }

      if (r instanceof FieldCondition) {
        sb.append(buildCondition((FieldCondition) r, params, paramIdx));
      } else if (r instanceof DisjunctionCondition) {
        DisjunctionCondition dc = (DisjunctionCondition) r;
        sb.append("(");
        for (int j = 0; j < dc.getQueries().length; j++) {
          if (j > 0) {
            sb.append(" OR ");
          }
          buildWhereClauseAndPopulateParams(dc.getQueries()[j], params, paramIdx, sb);
        }
        sb.append(")");
      } else {
        throw new IllegalStateException("Unsupported condition: " + r);
      }
    }
    sb.append(")");
  }

  private ConditionConverter<BooleanEqRestriction> booleanEqRestriction =
      new ConditionConverter<BooleanEqRestriction>() {
        @Override
        public String convert(
            BooleanEqRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.isNegative() ? !r.getValue() : r.getValue());
          return underscoredFieldName + " = :" + pn;
        }
      };

  private ConditionConverter<IsNullRestriction> isNullRestriction =
      new ConditionConverter<IsNullRestriction>() {
        @Override
        public String convert(
            IsNullRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          return underscoredFieldName + (r.isNegative() ? " IS NOT NULL" : " IS NULL");
        }
      };

  private ConditionConverter<NumberBetweenRestriction> numberBetweenRestriction =
      new ConditionConverter<NumberBetweenRestriction>() {
        @Override
        public String convert(
            NumberBetweenRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pnLower = pname(paramIdx);
          String pnUpper = pname(paramIdx);
          params.addValue(pnLower, r.getLowerBound());
          params.addValue(pnUpper, r.getUpperBound());
          if (!r.isNegative()) {
            return String.format(
                "(%s BETWEEN :%s AND :%s)", underscoredFieldName, pnLower, pnUpper);
          } else {
            return String.format(
                "(%s < :%s OR :%s < %s)",
                underscoredFieldName, pnLower, pnUpper, underscoredFieldName);
          }
        }
      };

  private ConditionConverter<StringBetweenRestriction> stringBetweenRestriction =
      new ConditionConverter<StringBetweenRestriction>() {
        @Override
        public String convert(
            StringBetweenRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pnLower = pname(paramIdx);
          String pnUpper = pname(paramIdx);
          params.addValue(pnLower, r.getLowerBound());
          params.addValue(pnUpper, r.getUpperBound());
          if (!r.isNegative()) {
            return String.format(
                "(%s BETWEEN :%s AND :%s)", underscoredFieldName, pnLower, pnUpper);
          } else {
            return String.format(
                "(%s < :%s OR :%s < %s)",
                underscoredFieldName, pnLower, pnUpper, underscoredFieldName);
          }
        }
      };

  private ConditionConverter<StringLengthBetweenRestriction> stringLengthBetweenRestriction =
      new ConditionConverter<StringLengthBetweenRestriction>() {
        @Override
        public String convert(
            StringLengthBetweenRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pnLower = pname(paramIdx);
          String pnUpper = pname(paramIdx);
          params.addValue(pnLower, r.getLowerBound());
          params.addValue(pnUpper, r.getUpperBound());
          return String.format(
              "(CHAR_LENGTH(%s) BETWEEN :%s AND :%s)", underscoredFieldName, pnLower, pnUpper);
        }
      };

  private ConditionConverter<NumberEqRestriction> numberEqRestriction =
      new ConditionConverter<NumberEqRestriction>() {
        @Override
        public String convert(
            NumberEqRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue());
          return underscoredFieldName + (r.isNegative() ? " != :" : " = :") + pn;
        }
      };

  private ConditionConverter<NumberGreaterOrEqualRestriction> numberGreaterOrEqualRestriction =
      new ConditionConverter<NumberGreaterOrEqualRestriction>() {
        @Override
        public String convert(
            NumberGreaterOrEqualRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue());
          return underscoredFieldName + (r.isNegative() ? " < :" : " >= :") + pn;
        }
      };

  private ConditionConverter<NumberOneOfRestriction> numberOneOfRestriction =
      new ConditionConverter<NumberOneOfRestriction>() {
        @Override
        public String convert(
            NumberOneOfRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValues());
          return underscoredFieldName + (r.isNegative() ? " NOT IN (:" : " IN (:") + pn + ")";
        }
      };

  private ConditionConverter<StringContainsRestriction> stringContainsRestriction =
      new ConditionConverter<StringContainsRestriction>() {
        @Override
        public String convert(
            StringContainsRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, "%" + r.getValue() + "%");
          String ret = underscoredFieldName + (r.isNegative() ? " NOT LIKE :" : " LIKE :") + pn;
          return ret;
        }
      };

  private ConditionConverter<StringEqRestriction> stringEqRestriction =
      new ConditionConverter<StringEqRestriction>() {
        @Override
        public String convert(
            StringEqRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue());
          String ret = underscoredFieldName + (r.isNegative() ? " != :" : " = :") + pn;
          return ret;
        }
      };

  private ConditionConverter<StringOneOfRestriction> stringOneOfRestriction =
      new ConditionConverter<StringOneOfRestriction>() {
        @Override
        public String convert(
            StringOneOfRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValues());
          String ret = underscoredFieldName + (r.isNegative() ? " NOT IN (:" : " IN (:") + pn + ")";
          return ret;
        }
      };

  private ConditionConverter<StringStartsWithRestriction> stringStartsWithRestriction =
      new ConditionConverter<StringStartsWithRestriction>() {
        @Override
        public String convert(
            StringStartsWithRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue() + "%");
          return underscoredFieldName + (r.isNegative() ? " NOT LIKE :" : " LIKE :") + pn;
        }
      };

  private ConditionConverter<StringLessOrEqualsRestriction> stringLessOrEqualsRestriction =
      new ConditionConverter<StringLessOrEqualsRestriction>() {
        @Override
        public String convert(
            StringLessOrEqualsRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue());
          return underscoredFieldName + (!r.isNegative() ? " <= :" : " > :") + pn;
        }
      };

  private ConditionConverter<NumberLessOrEqualsRestriction> numberLessOrEqualsRestriction =
      new ConditionConverter<NumberLessOrEqualsRestriction>() {
        @Override
        public String convert(
            NumberLessOrEqualsRestriction r,
            MapSqlParameterSource params,
            AtomicInteger paramIdx,
            String underscoredFieldName) {
          String pn = pname(paramIdx);
          params.addValue(pn, r.getValue());
          return underscoredFieldName + (!r.isNegative() ? " <= :" : " > :") + pn;
        }
      };

  @SuppressWarnings({"rawtypes", "unchecked"})
  private String buildCondition(
      FieldCondition c, MapSqlParameterSource params, AtomicInteger paramIdx) {
    String underscoredFieldName = underscore(c.getFieldName());

    ConditionConverter converter = converters.get(c.getRestriction().getClass());
    if (converter == null) {
      throw new IllegalStateException("Unsupported restriction: " + c);
    }

    return converter.convert(c.getRestriction(), params, paramIdx, underscoredFieldName);
  }

  protected String pname(AtomicInteger paramIdx) {
    return "arg" + paramIdx.incrementAndGet();
  }

  public static String underscore(String name) {
    StringBuilder result = new StringBuilder();
    if (name != null && name.length() > 0) {
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
}
