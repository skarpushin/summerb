package org.summerb.easycrud.sql_builder.postgres.restrictions;

import static org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl.buildNextParamName;

import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.query.restrictions.In;

/** Case-insensitive for Strings, workaround Postgres specifics */
public class InRestrictionToNativeSql
    extends org.summerb.easycrud.sql_builder.mysql.restrictions.InRestrictionToNativeSql {

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
