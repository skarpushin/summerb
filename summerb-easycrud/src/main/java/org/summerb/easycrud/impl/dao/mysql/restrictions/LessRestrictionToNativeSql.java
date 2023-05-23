package org.summerb.easycrud.impl.dao.mysql.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Less;

public class LessRestrictionToNativeSql implements RestrictionToNativeSql<Less> {

  @Override
  public String convert(
      Less restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(pn, restriction.getValue());

    if (restriction.isNot()) {
      if (!restriction.isIncludeBoundary()) {
        return String.format("(%s >= :%s)", underscoredFieldName, pn);
      } else {
        return String.format("(%s > :%s)", underscoredFieldName, pn);
      }
    } else {
      if (restriction.isIncludeBoundary()) {
        return String.format("(%s <= :%s)", underscoredFieldName, pn);
      } else {
        return String.format("(%s < :%s)", underscoredFieldName, pn);
      }
    }
  }
}
