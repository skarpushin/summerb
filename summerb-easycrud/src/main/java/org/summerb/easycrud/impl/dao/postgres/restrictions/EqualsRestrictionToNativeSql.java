package org.summerb.easycrud.impl.dao.postgres.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Equals;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

/** Case-insensitive for Strings, workaround Postgress specifics */
public class EqualsRestrictionToNativeSql
    extends org.summerb.easycrud.impl.dao.mysql.restrictions.EqualsRestrictionToNativeSql {

  @Override
  public String convert(
      Equals restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    if (!(restriction.getValue() instanceof CharSequence)) {
      return super.convert(
          restriction, params, nextParameterIndex, underscoredFieldName, sqlTypeOverrides);
    }

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(pn, restriction.getValue());
    return String.format(
        "lower(%s) %s :%s", underscoredFieldName, restriction.isNot() ? " != " : " = ", pn);
  }
}
