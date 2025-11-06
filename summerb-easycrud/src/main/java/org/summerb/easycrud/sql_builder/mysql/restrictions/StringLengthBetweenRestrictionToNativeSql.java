package org.summerb.easycrud.sql_builder.mysql.restrictions;

import static org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.StringLengthBetween;

public class StringLengthBetweenRestrictionToNativeSql
    implements RestrictionToNativeSql<StringLengthBetween> {

  @Override
  public String convert(
      StringLengthBetween restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pnLower = buildNextParamName(nextParameterIndex);
    String pnUpper = buildNextParamName(nextParameterIndex);
    params.addValue(pnLower, restriction.getLowerBoundary());
    params.addValue(pnUpper, restriction.getUpperBoundary());
    if (restriction.isNot()) {
      return String.format(
          "(CHAR_LENGTH(%s) < :%s OR :%s < CHAR_LENGTH(%s))",
          underscoredFieldName, pnLower, pnUpper, underscoredFieldName);
    } else {
      return String.format(
          "CHAR_LENGTH(%s) BETWEEN :%s AND :%s", underscoredFieldName, pnLower, pnUpper);
    }
  }
}
