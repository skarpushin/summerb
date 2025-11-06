package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.In;

public class InRestrictionToNativeSql extends RestrictionToNativeSqlTemplate<In> {

  @Override
  public String convert(
      In restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    String pn = addValues(params, nextParameterIndex, restriction.getValues(), sqlTypeOverrides);
    return underscoredFieldName + (restriction.isNot() ? " NOT IN " : " IN ") + "(:" + pn + ")";
  }
}
