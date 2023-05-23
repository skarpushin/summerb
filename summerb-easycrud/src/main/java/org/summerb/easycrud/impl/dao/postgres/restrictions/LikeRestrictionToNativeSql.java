package org.summerb.easycrud.impl.dao.postgres.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Like;
import org.summerb.easycrud.impl.dao.mysql.restrictions.RestrictionToNativeSql;

public class LikeRestrictionToNativeSql implements RestrictionToNativeSql<Like> {

  @Override
  public String convert(
      Like restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(
        pn,
        (restriction.isAddPrefixWildcard() ? "%" : "")
            + restriction.getSubString()
            + (restriction.isAddPostfixWildcard() ? "%" : ""));

    if (restriction.isNot()) {
      return underscoredFieldName + " NOT ILIKE :" + pn;
    } else {
      return underscoredFieldName + " ILIKE :" + pn;
    }
  }
}
