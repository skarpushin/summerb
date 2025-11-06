package org.summerb.easycrud.sql_builder.postgres.restrictions;

import static org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.Equals;

/** Case-insensitive for Strings, workaround Postgres specifics */
public class EqualsRestrictionToNativeSql
    extends org.summerb.easycrud.sql_builder.mysql.restrictions.EqualsRestrictionToNativeSql {

  @Override
  public String convert(
      Equals restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    if (!(restriction.getValue() instanceof String str)) {
      return super.convert(
          restriction, params, nextParameterIndex, underscoredFieldName, sqlTypeOverrides);
    }

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(pn, str.toLowerCase());
    return String.format(
        "lower(%s) %s :%s", underscoredFieldName, restriction.isNot() ? " != " : " = ", pn);
  }
}
