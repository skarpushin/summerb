package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.base.Restriction;

public interface RestrictionToNativeSql<T extends Restriction> {

  String convert(
      T restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides);
}
