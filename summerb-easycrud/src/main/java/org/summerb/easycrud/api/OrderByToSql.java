package org.summerb.easycrud.api;

import org.summerb.easycrud.api.dto.OrderBy;

public interface OrderByToSql {
  String buildOrderBySubclause(OrderBy[] orderBy);
}
