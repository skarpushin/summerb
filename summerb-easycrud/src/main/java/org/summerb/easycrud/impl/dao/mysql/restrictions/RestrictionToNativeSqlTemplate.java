package org.summerb.easycrud.impl.dao.mysql.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;
import org.summerb.easycrud.impl.dao.SqlTypeOverride;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

public abstract class RestrictionToNativeSqlTemplate<T extends Restriction>
    implements RestrictionToNativeSql<T> {

  protected String addValue(
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      Object value,
      SqlTypeOverrides sqlTypeOverrides) {

    String parameterName = buildNextParamName(nextParameterIndex);

    // NOTE: Consider adding cahcing layer to improve performance
    SqlTypeOverride sqlTypeOverride = sqlTypeOverrides.findOverrideForValue(value);
    if (sqlTypeOverride == null) {
      params.addValue(parameterName, value);
    } else {
      params.addValue(parameterName, sqlTypeOverride.convert(value), sqlTypeOverride.getSqlType());
    }
    return parameterName;
  }

  protected String addValues(
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      Set<?> values,
      SqlTypeOverrides sqlTypeOverrides) {

    String parameterName = buildNextParamName(nextParameterIndex);

    Object firstValue = values.stream().filter(Objects::nonNull).findFirst().orElse(null);
    SqlTypeOverride sqlTypeOverride = sqlTypeOverrides.findOverrideForValue(firstValue);

    if (sqlTypeOverride == null) {
      params.addValue(parameterName, values);
    } else {
      if (!sqlTypeOverride.isConversionRequired()) {
        params.addValue(parameterName, values, sqlTypeOverride.getSqlType());
      } else {
        params.addValue(
            parameterName,
            values.stream().map(sqlTypeOverride::convert).collect(Collectors.toSet()),
            sqlTypeOverride.getSqlType());
      }
    }

    return parameterName;
  }
}
