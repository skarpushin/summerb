package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.Between;

public class BetweenRestrictionToNativeSql extends RestrictionToNativeSqlTemplate<Between> {

  @Override
  public String convert(
      Between restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pnLower =
        addValue(params, nextParameterIndex, restriction.getLowerBoundary(), sqlTypeOverrides);

    String pnUpper =
        addValue(params, nextParameterIndex, restriction.getUpperBoundary(), sqlTypeOverrides);

    if (!restriction.isNot()) {
      return String.format("%s BETWEEN :%s AND :%s", underscoredFieldName, pnLower, pnUpper);
    } else {
      return String.format(
          "(%s < :%s OR :%s < %s)", underscoredFieldName, pnLower, pnUpper, underscoredFieldName);
    }
  }
}
