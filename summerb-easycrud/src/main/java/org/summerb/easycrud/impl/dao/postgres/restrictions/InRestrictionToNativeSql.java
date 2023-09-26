package org.summerb.easycrud.impl.dao.postgres.restrictions;

import static org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.restrictions.In;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;

/** Case-insensitive for Strings, workaround Postgress specifics */
public class InRestrictionToNativeSql
    extends org.summerb.easycrud.impl.dao.mysql.restrictions.InRestrictionToNativeSql {

  @Override
  public String convert(
      In restriction,
      MapSqlParameterSource params,
      Supplier<Integer> nextParameterIndex,
      String underscoredFieldName,
      SqlTypeOverrides sqlTypeOverrides) {

    if (!(restriction.getValues().iterator().next() instanceof CharSequence)) {
      return super.convert(
          restriction, params, nextParameterIndex, underscoredFieldName, sqlTypeOverrides);
    }

    String pn = buildNextParamName(nextParameterIndex);
    params.addValue(
        pn,
        restriction.getValues().stream()
            .map(x -> x.toString().toLowerCase())
            .collect(Collectors.toList()));
    return String.format(
        "lower(%s) %s (:%s)", underscoredFieldName, restriction.isNot() ? " NOT IN " : " IN ", pn);
  }
}
