package org.summerb.easycrud.sql_builder.postgres;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.sql_builder.mysql.OrderByToSqlMySqlImpl;

public class OrderByToSqlPostgresImpl extends OrderByToSqlMySqlImpl {
  public OrderByToSqlPostgresImpl() {}

  @Override
  @VisibleForTesting
  public void appendCollation(StringBuilder ret, OrderBy orderBy) {
    if (StringUtils.hasText(orderBy.getCollate())) {
      ret.append(" COLLATE ").append("\"").append(orderBy.getCollate()).append("\"");
    }
  }

  @Override
  @VisibleForTesting
  public void appendNullsHandling(OrderBy orderBy, StringBuilder ret) {
    if (orderBy.getNullsLast() != null) {
      ret.append(orderBy.getNullsLast() ? " NULLS LAST" : " NULLS FIRST");
    }
  }
}
