package org.summerb.easycrud.impl.dao.mysql.restrictions;

import java.util.function.Supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

public interface RestrictionToNativeSql<T extends Restriction> {

  String convert(
      T restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides);
}
