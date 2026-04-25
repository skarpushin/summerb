package org.summerb.easycrud.sql_builder.mysql.restrictions;

import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.base.Restriction;

public interface RestrictionToNativeSql<T extends Restriction> {

  /**
   * Convert restriction to native SQL.
   *
   * @param restriction restriction
   * @param params parameters
   * @param nextParameterIndex next parameter index supplier
   * @param underscoredFieldName underscored field name
   * @param sqlTypeOverrides SQL type overrides
   * @return native SQL string
   */
  String convert(
      T restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides);
}
