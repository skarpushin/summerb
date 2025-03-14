package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Less;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

public class LessRestrictionToNativeSql extends RestrictionToNativeSqlTemplate<Less> {

  @Override
  public String convert(
      Less restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pn = addValue(params, nextParameterIndex, restriction.getValue(), sqlTypeOverrides);

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
