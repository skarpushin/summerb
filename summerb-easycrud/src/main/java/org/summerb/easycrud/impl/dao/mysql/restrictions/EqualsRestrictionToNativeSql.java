package org.summerb.easycrud.impl.dao.mysql.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Equals;

public class EqualsRestrictionToNativeSql implements RestrictionToNativeSql<Equals> {

  @Override
  public String convert(
      Equals restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(pn, restriction.getValue());
    return underscoredFieldName + (restriction.isNot() ? " != " : " = ") + ":" + pn;
  }
}
