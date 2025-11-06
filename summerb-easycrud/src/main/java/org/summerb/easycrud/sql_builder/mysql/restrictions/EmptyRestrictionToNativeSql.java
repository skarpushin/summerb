package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.Empty;

public class EmptyRestrictionToNativeSql implements RestrictionToNativeSql<Empty> {

  @Override
  public String convert(
      Empty restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    if (!restriction.isNot()) {
      return String.format("CHAR_LENGTH(%s) = 0", underscoredFieldName);
    } else {
      return String.format("CHAR_LENGTH(%s) > 0", underscoredFieldName);
    }
  }
}
