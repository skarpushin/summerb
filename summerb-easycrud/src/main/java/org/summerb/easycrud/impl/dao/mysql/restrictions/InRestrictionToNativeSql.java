package org.summerb.easycrud.impl.dao.mysql.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.In;

public class InRestrictionToNativeSql implements RestrictionToNativeSql<In> {

  @Override
  public String convert(
      In restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(pn, restriction.getValues());
    return underscoredFieldName + (restriction.isNot() ? " NOT IN " : " IN ") + "(:" + pn + ")";
  }
}
