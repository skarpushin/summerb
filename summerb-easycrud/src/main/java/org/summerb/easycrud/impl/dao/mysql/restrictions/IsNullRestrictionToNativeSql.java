package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.IsNull;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

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
