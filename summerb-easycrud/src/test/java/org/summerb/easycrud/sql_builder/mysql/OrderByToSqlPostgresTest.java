package org.summerb.easycrud.sql_builder.mysql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.sql_builder.postgres.OrderByToSqlPostgresImpl;

public class OrderByToSqlPostgresTest {

  private OrderByToSqlPostgresImpl orderByToSql;

  @BeforeEach
  void setUp() {
    orderByToSql = new OrderByToSqlPostgresImpl();
  }

  @Test
  void buildOrderBySubclause_shouldReturnEmptyStringForNullArray() {
    String result = orderByToSql.buildOrderBySubclause(null);
    assertEquals("", result);
  }

  @Test
  void buildOrderBySubclause_shouldReturnEmptyStringForEmptyArray() {
    String result = orderByToSql.buildOrderBySubclause(new OrderBy[0]);
    assertEquals("", result);
  }

  @Test
  void buildOrderBySubclause_shouldReturnEmptyStringForArrayWithNullElements() {
    OrderBy[] orderBys = new OrderBy[] {null, null};
    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("", result);
  }

  @Test
  void buildOrderBySubclause_shouldReturnEmptyStringForArrayWithEmptyFieldNames() {
    OrderBy orderBy1 = new OrderBy();
    OrderBy orderBy2 = new OrderBy();
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithFieldNameOnly() {
    OrderBy orderBy = OrderBy.Asc("userName");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithDirection() {
    OrderBy orderBy = OrderBy.Desc("createdAt");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY created_at DESC", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithCollate() {
    OrderBy orderBy = OrderBy.Asc("userName").withCollate("utf8mb4_bin");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name COLLATE \"utf8mb4_bin\"", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithNullsLast() {
    OrderBy orderBy = OrderBy.Asc("userName").nullsLast();
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name NULLS LAST", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithNullsFirst() {
    OrderBy orderBy = OrderBy.Asc("userName").nullsFirst();
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name NULLS FIRST", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleSingleOrderByWithAllComponents() {
    OrderBy orderBy = OrderBy.Desc("userName").withCollate("utf8mb4_unicode_ci").nullsLast();
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name COLLATE \"utf8mb4_unicode_ci\" DESC NULLS LAST", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleMultipleOrderBys() {
    OrderBy orderBy1 = OrderBy.Asc("firstName");
    OrderBy orderBy2 = OrderBy.Desc("lastName").nullsLast();
    OrderBy orderBy3 = OrderBy.Asc("age").withCollate("utf8mb4_bin");
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2, orderBy3};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals(
        "\nORDER BY first_name, last_name DESC NULLS LAST, age COLLATE \"utf8mb4_bin\"", result);
  }

  @Test
  void buildOrderBySubclause_shouldSkipNullElementsInArray() {
    OrderBy orderBy1 = OrderBy.Asc("firstName");
    OrderBy orderBy2 = null;
    OrderBy orderBy3 = OrderBy.Desc("lastName");
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2, orderBy3};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY first_name, last_name DESC", result);
  }

  @Test
  void buildOrderBySubclause_shouldThrowExceptionOnInvalidFieldName() {
    OrderBy orderBy2 = new OrderBy();
    assertThrows(IllegalArgumentException.class, () -> orderBy2.setFieldName(""));
  }

  @Test
  void buildOrderBySubclause_shouldHandleMixedValidAndInvalidElements() {
    OrderBy orderBy1 = OrderBy.Asc("validField");
    OrderBy orderBy2 = null;
    OrderBy orderBy3 = new OrderBy();
    OrderBy orderBy4 = OrderBy.Desc("anotherValidField").nullsFirst();
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2, orderBy3, orderBy4};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY valid_field, another_valid_field DESC NULLS FIRST", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleFieldNameWithUnderscores() {
    OrderBy orderBy = OrderBy.Asc("user_name");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    // Should still apply snake_case conversion, though it might be redundant
    assertEquals("\nORDER BY user_name", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleFieldNameWithDots() {
    OrderBy orderBy = OrderBy.Asc("users.name");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY users.name", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleNullDirection() {
    OrderBy orderBy = new OrderBy();
    orderBy.setFieldName("userName");
    // direction is null
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleNullCollate() {
    OrderBy orderBy = OrderBy.Asc("userName");
    orderBy.setCollate(null);
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleNullNullsLast() {
    OrderBy orderBy = OrderBy.Asc("userName");
    orderBy.setNullsLast(null);
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name", result);
  }

  @Test
  void appendDirection_shouldAppendSpaceAndDirection() {
    OrderBy orderBy = OrderBy.Desc("test");
    StringBuilder sb = new StringBuilder("test_field");

    orderByToSql.appendDirection(orderBy, sb);

    assertEquals("test_field DESC", sb.toString());
  }

  @Test
  void appendDirection_shouldHandleAscDirection() {
    OrderBy orderBy = OrderBy.Asc("test");
    StringBuilder sb = new StringBuilder("test_field");

    orderByToSql.appendDirection(orderBy, sb);

    assertEquals("test_field", sb.toString());
  }

  @Test
  void appendCollation_shouldAppendCollateClause() {
    OrderBy orderBy = OrderBy.Asc("test").withCollate("utf8mb4_bin");
    StringBuilder sb = new StringBuilder("test_field");

    orderByToSql.appendCollation(sb, orderBy);

    assertEquals("test_field COLLATE \"utf8mb4_bin\"", sb.toString());
  }

  @Test
  void appendNullsHandling_shouldAppendNullsLast() {
    OrderBy orderBy = OrderBy.Asc("test").nullsLast();
    StringBuilder sb = new StringBuilder("test_field");

    orderByToSql.appendNullsHandling(orderBy, sb);

    assertEquals("test_field NULLS LAST", sb.toString());
  }

  @Test
  void appendNullsHandling_shouldAppendNullsFirst() {
    OrderBy orderBy = OrderBy.Asc("test").nullsFirst();
    StringBuilder sb = new StringBuilder("test_field");

    orderByToSql.appendNullsHandling(orderBy, sb);

    assertEquals("test_field NULLS FIRST", sb.toString());
  }

  @Test
  void buildOrderBySubclause_shouldMaintainOrderOfMultipleFields() {
    OrderBy orderBy1 = OrderBy.Asc("firstName");
    OrderBy orderBy2 = OrderBy.Desc("lastName");
    OrderBy orderBy3 = OrderBy.Asc("age");
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2, orderBy3};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY first_name, last_name DESC, age", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleComplexFieldNames() {
    OrderBy orderBy = OrderBy.Asc("userAccountId");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_account_id", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleAllNullsLastScenarios() {
    // Test that the static defaultNullsLast doesn't affect the output when null
    OrderBy.defaultNullsLast = null;
    OrderBy orderBy = OrderBy.Asc("testField");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY test_field", result);
  }

  @Test
  void buildOrderBySubclause_shouldHandleCaseSensitiveCollate() {
    OrderBy orderBy = OrderBy.Asc("userName").withCollate("UTF8MB4_BIN");
    OrderBy[] orderBys = new OrderBy[] {orderBy};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("\nORDER BY user_name COLLATE \"UTF8MB4_BIN\"", result);
  }

  @Test
  void buildOrderBySubclause_shouldNotAddOrderByWhenAllElementsAreInvalid() {
    OrderBy orderBy1 = new OrderBy();
    OrderBy orderBy2 = null;
    OrderBy[] orderBys = new OrderBy[] {orderBy1, orderBy2};

    String result = orderByToSql.buildOrderBySubclause(orderBys);
    assertEquals("", result);
  }
}
