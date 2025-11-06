package org.summerb.easycrud.sql_builder.postgres.restrictions;

import static org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.Like;
import org.summerb.easycrud.sql_builder.mysql.restrictions.RestrictionToNativeSql;

public class LikeRestrictionToNativeSql implements RestrictionToNativeSql<Like> {

  @Override
  public String convert(
      Like restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

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
