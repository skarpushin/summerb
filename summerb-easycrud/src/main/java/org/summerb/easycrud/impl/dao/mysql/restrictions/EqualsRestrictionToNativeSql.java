package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.Equals;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

public class EqualsRestrictionToNativeSql extends RestrictionToNativeSqlTemplate<Equals> {

  @Override
  public String convert(
      Equals restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pn = addValue(params, nextParameterIndex, restriction.getValue(), sqlTypeOverrides);

    return underscoredFieldName + (restriction.isNot() ? " != " : " = ") + ":" + pn;
  }
}
