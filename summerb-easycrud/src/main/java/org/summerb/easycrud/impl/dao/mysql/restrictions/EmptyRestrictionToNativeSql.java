package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Empty;

public class EmptyRestrictionToNativeSql implements RestrictionToNativeSql<Empty> {

  @Override
  public String convert(
      Empty restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName) {

    if (!restriction.isNot()) {
      return String.format("(CHAR_LENGTH(%s) = 0)", underscoredFieldName);
    } else {
      return String.format("(CHAR_LENGTH(%s) > 0)", underscoredFieldName);
    }
  }
}
