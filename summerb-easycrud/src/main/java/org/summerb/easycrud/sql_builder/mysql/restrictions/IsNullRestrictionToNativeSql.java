package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.IsNull;

public class IsNullRestrictionToNativeSql implements RestrictionToNativeSql<IsNull> {

  @Override
  public String convert(
      IsNull restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    return underscoredFieldName + (restriction.isNot() ? " IS NOT NULL" : " IS NULL");
  }
}
