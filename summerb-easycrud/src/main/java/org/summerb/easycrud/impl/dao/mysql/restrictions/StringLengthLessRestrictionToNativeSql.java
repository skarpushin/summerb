package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.StringLengthLess;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

public class StringLengthLessRestrictionToNativeSql
    extends RestrictionToNativeSqlTemplate<StringLengthLess> {

  @Override
  public String convert(
      StringLengthLess restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pn = addValue(params, nextParameterIndex, restriction.getValue(), sqlTypeOverrides);

    if (restriction.isNot()) {
      if (!restriction.isIncludeBoundary()) {
        return String.format("(CHAR_LENGTH(%s) >= :%s)", underscoredFieldName, pn);
      } else {
        return String.format("(CHAR_LENGTH(%s) > :%s)", underscoredFieldName, pn);
      }
    } else {
      if (restriction.isIncludeBoundary()) {
        return String.format("(CHAR_LENGTH(%s) <= :%s)", underscoredFieldName, pn);
      } else {
        return String.format("(CHAR_LENGTH(%s) < :%s)", underscoredFieldName, pn);
      }
    }
  }
}
