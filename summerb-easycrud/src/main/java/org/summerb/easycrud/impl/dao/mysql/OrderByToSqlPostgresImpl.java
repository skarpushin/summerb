package org.summerb.easycrud.impl.dao.mysql;

import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.dto.OrderBy;

public class OrderByToSqlPostgresImpl extends OrderByToSqlMySqlImpl {
  @Override
  protected void appendCollation(StringBuilder ret, OrderBy orderBy) {
    if (StringUtils.hasText(orderBy.getCollate())) {
      ret.append(" COLLATE ").append("'").append(orderBy.getCollate()).append("'");
    }
  }
}
