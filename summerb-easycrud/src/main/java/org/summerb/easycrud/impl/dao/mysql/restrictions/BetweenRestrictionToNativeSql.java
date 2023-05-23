package org.summerb.easycrud.impl.dao.mysql.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Between;

public class BetweenRestrictionToNativeSql implements RestrictionToNativeSql<Between> {

  @Override
  public String convert(
      Between restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    String pnLower = buildNextParamName(nextParameterIndex);
    String pnUpper = buildNextParamName(nextParameterIndex);
    params.addValue(pnLower, restriction.getLowerBoundary());
    params.addValue(pnUpper, restriction.getUpperBoundary());
    if (!restriction.isNot()) {
      return String.format("(%s BETWEEN :%s AND :%s)", underscoredFieldName, pnLower, pnUpper);
    } else {
      return String.format(
          "(%s < :%s OR :%s < %s)", underscoredFieldName, pnLower, pnUpper, underscoredFieldName);
    }
  }
}
