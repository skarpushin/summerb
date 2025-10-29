package org.summerb.easycrud.impl.dao.mysql;

import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.OrderByToSql;
import org.summerb.easycrud.api.dto.OrderBy;

public class OrderByToSqlMySqlImpl implements OrderByToSql {
  @Override
  public String buildOrderBySubclause(OrderBy[] orderByArr) {
    if (orderByArr == null || orderByArr.length == 0) {
      return "";
    }

    StringBuilder ret = new StringBuilder();
    for (OrderBy orderBy : orderByArr) {
      if (orderBy == null || !StringUtils.hasText(orderBy.getFieldName())) {
        continue;
      }

      if (!ret.isEmpty()) {
        ret.append(", ");
      }

      appendFieldName(ret, orderBy);
      appendCollation(ret, orderBy);
      appendDirection(orderBy, ret);
      appendNullsHandling(orderBy, ret);
    }
    return ret.isEmpty() ? "" : " ORDER BY " + ret;
  }

  protected void appendFieldName(StringBuilder ret, OrderBy orderBy) {
    ret.append(QueryToSqlMySqlImpl.snakeCase(orderBy.getFieldName()));
  }

  protected void appendNullsHandling(OrderBy orderBy, StringBuilder ret) {
    if (orderBy.getNullsLast() != null) {
      ret.append(orderBy.getNullsLast() ? " NULLS LAST" : " NULLS FIRST");
    }
  }

  protected void appendDirection(OrderBy orderBy, StringBuilder ret) {
    if (OrderBy.ORDER_ASC.equals(orderBy.getDirection())) {
      return;
    }
    if (orderBy.getDirection() != null) {
      ret.append(" ").append(orderBy.getDirection());
    }
  }

  protected void appendCollation(StringBuilder ret, OrderBy orderBy) {
    if (StringUtils.hasText(orderBy.getCollate())) {
      ret.append(" COLLATE ").append(orderBy.getCollate());
    }
  }
}
