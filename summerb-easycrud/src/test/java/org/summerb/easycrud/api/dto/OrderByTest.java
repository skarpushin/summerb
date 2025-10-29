package org.summerb.easycrud.api.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderByTest {
  @BeforeEach
  void setUp() {
    OrderBy.defaultNullsLast = null;
  }

  @Test
  void asc_shouldCreateInstanceWithAscendingDirection() {
    OrderBy orderBy = OrderBy.Asc("name");

    assertEquals("name", orderBy.getFieldName());
    assertEquals(OrderBy.ORDER_ASC, orderBy.getDirection());
    assertNull(orderBy.getNullsLast());
    assertNull(orderBy.getCollate());
  }

  @Test
  void desc_shouldCreateInstanceWithDescendingDirection() {
    OrderBy orderBy = OrderBy.Desc("createdDate");

    assertEquals("createdDate", orderBy.getFieldName());
    assertEquals(OrderBy.ORDER_DESC, orderBy.getDirection());
    assertNull(orderBy.getNullsLast());
    assertNull(orderBy.getCollate());
  }

  @Test
  void factoryMethods_shouldHandleNullFieldName() {
    assertThrows(IllegalArgumentException.class, () -> OrderBy.Asc(null));
    assertThrows(IllegalArgumentException.class, () -> OrderBy.Desc(null));
  }

  @Test
  void format_shouldCreateBasicOrderByString() {
    OrderBy orderBy = OrderBy.Asc("name");
    String result = orderBy.format();

    assertEquals("name,ASC", result);
  }

  @Test
  void format_shouldIncludeCollateWhenPresent() {
    OrderBy orderBy = OrderBy.Asc("name").withCollate("unicode");
    String result = orderBy.format();

    assertEquals("name,ASC,collate unicode", result);
  }

  @Test
  void format_shouldIncludeNullsLastWhenTrue() {
    OrderBy orderBy = OrderBy.Asc("name").nullsLast();
    String result = orderBy.format();

    assertEquals("name,ASC,nulls last", result);
  }

  @Test
  void format_shouldIncludeNullsFirstWhenFalse() {
    OrderBy orderBy = OrderBy.Asc("name").nullsFirst();
    String result = orderBy.format();

    assertEquals("name,ASC,nulls first", result);
  }

  @Test
  void format_shouldHandleAllComponentsTogether() {
    OrderBy orderBy = OrderBy.Desc("name").withCollate("unicode").nullsLast();
    String result = orderBy.format();

    assertEquals("name,DESC,collate unicode,nulls last", result);
  }

  @Test
  void format_shouldHandleNullFieldName() {
    OrderBy orderBy = new OrderBy();
    orderBy.setDirection(OrderBy.ORDER_ASC);

    assertThrows(IllegalArgumentException.class, orderBy::format);
  }

  @Test
  void format_shouldHandleNullDirection() {
    OrderBy orderBy = new OrderBy();
    orderBy.setFieldName("name");
    // direction is null

    assertEquals("name", orderBy.format());
  }

  @Test
  void parse_shouldHandleBasicOrderByString() {
    String orderByStr = "name,ASC";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals("name", result.getFieldName());
    assertEquals(OrderBy.ORDER_ASC, result.getDirection());
    assertNull(result.getCollate());
    assertNull(result.getNullsLast());
  }

  @Test
  void parse_shouldHandleDescendingDirection() {
    String orderByStr = "createdDate,DESC";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals("createdDate", result.getFieldName());
    assertEquals(OrderBy.ORDER_DESC, result.getDirection());
  }

  @Test
  void parse_shouldHandleCaseInsensitiveDirection() {
    String orderByStr = "name,desc";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals(OrderBy.ORDER_DESC, result.getDirection());
  }

  @Test
  void parse_shouldHandleCollate() {
    String orderByStr = "name,ASC,collate unicode";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals("name", result.getFieldName());
    assertEquals("unicode", result.getCollate());
  }

  @Test
  void parse_shouldHandleNullsLast() {
    String orderByStr = "name,ASC,nulls last";
    OrderBy result = OrderBy.parse(orderByStr);

    assertTrue(result.getNullsLast());
  }

  @Test
  void parse_shouldHandleNullsFirst() {
    String orderByStr = "name,ASC,nulls first";
    OrderBy result = OrderBy.parse(orderByStr);

    assertFalse(result.getNullsLast());
  }

  @Test
  void parse_shouldHandleAllComponents() {
    String orderByStr = "name,DESC,collate unicode,nulls last";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals("name", result.getFieldName());
    assertEquals(OrderBy.ORDER_DESC, result.getDirection());
    assertEquals("unicode", result.getCollate());
    assertTrue(result.getNullsLast());
  }

  @Test
  void parse_shouldHandleComponentsInDifferentOrder() {
    assertEquals(
        OrderBy.parse("name,DESC,collate unicode,nulls last"),
        OrderBy.Desc("name").withCollate("unicode").nullsLast());

    assertEquals(
        OrderBy.parse("name,DESC,nulls last,collate unicode"),
        OrderBy.parse("name,DESC,collate unicode,nulls last"));
  }

  @Test
  void parse_shouldThrowExceptionForInvalidDirection() {
    String orderByStr = "name,INVALID";

    assertThrows(IllegalArgumentException.class, () -> OrderBy.parse(orderByStr));
  }

  @Test
  void parse_shouldBeOkWithFieldOnly() {
    String orderByStr = "name";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals(orderByStr, result.getFieldName());
    assertNull(result.getDirection());
    assertNull(result.getCollate());
    assertNull(result.getNullsLast());
  }

  @Test
  void parse_shouldThrowExceptionForTooManyParts() {
    String orderByStr = "name,ASC,collate unicode,nulls last,extra";

    assertThrows(IllegalArgumentException.class, () -> OrderBy.parse(orderByStr));
  }

  @Test
  void parse_shouldThrowExceptionForInvalidNullsValue() {
    String orderByStr = "name,ASC,nulls middle";

    assertThrows(IllegalArgumentException.class, () -> OrderBy.parse(orderByStr));
  }

  @Test
  void parse_shouldHandleWhitespace() {
    String orderByStr = "  name  ,  DESC  ,  collate   unicode  ,  nulls last  ";
    OrderBy result = OrderBy.parse(orderByStr);

    assertEquals("name", result.getFieldName());
    assertEquals(OrderBy.ORDER_DESC, result.getDirection());
    assertEquals("unicode", result.getCollate());
    assertTrue(result.getNullsLast());
  }

  @Test
  void nullsLast_shouldSetNullsLastToTrue() {
    OrderBy orderBy = OrderBy.Asc("name").nullsLast();
    assertTrue(orderBy.getNullsLast());
  }

  @Test
  void nullsFirst_shouldSetNullsLastToFalse() {
    OrderBy orderBy = OrderBy.Asc("name").nullsFirst();
    assertFalse(orderBy.getNullsLast());
  }

  @Test
  void nullsDefault_shouldSetNullsLastToNull() {
    OrderBy orderBy = OrderBy.Asc("name").nullsLast().nullsDefault();
    assertNull(orderBy.getNullsLast());
  }

  @Test
  void withNullsLast_shouldSetNullsLast() {
    OrderBy orderBy = OrderBy.Asc("name").withNullsLast(false);
    assertFalse(orderBy.getNullsLast());
  }

  @Test
  void equals_shouldReturnTrueForSameValues() {
    OrderBy order1 = OrderBy.Asc("name").withCollate("unicode").nullsLast();
    OrderBy order2 = OrderBy.Asc("name").withCollate("unicode").nullsLast();

    assertEquals(order1, order2);
    assertEquals(order1.hashCode(), order2.hashCode());
  }

  @Test
  void equals_shouldReturnFalseForDifferentFieldName() {
    OrderBy order1 = OrderBy.Asc("name");
    OrderBy order2 = OrderBy.Asc("different");

    assertNotEquals(order1, order2);
  }

  @Test
  void equals_shouldReturnFalseForDifferentDirection() {
    OrderBy order1 = OrderBy.Asc("name");
    OrderBy order2 = OrderBy.Desc("name");

    assertNotEquals(order1, order2);
  }

  @Test
  void equals_shouldReturnFalseForDifferentCollate() {
    OrderBy order1 = OrderBy.Asc("name").withCollate("unicode");
    OrderBy order2 = OrderBy.Asc("name").withCollate("other");

    assertNotEquals(order1, order2);
  }

  @Test
  void equals_shouldReturnFalseForDifferentNullsLast() {
    OrderBy order1 = OrderBy.Asc("name").nullsLast();
    OrderBy order2 = OrderBy.Asc("name").nullsFirst();

    assertNotEquals(order1, order2);
  }

  @Test
  void equals_shouldReturnFalseForNull() {
    OrderBy order = OrderBy.Asc("name");
    assertNotEquals(null, order);
  }

  @Test
  void equals_shouldReturnFalseForDifferentClass() {
    OrderBy order = OrderBy.Asc("name");
    //noinspection AssertBetweenInconvertibleTypes -- that is intentional!
    assertNotEquals("not an OrderBy", order);
  }

  @Test
  void shouldHandleCollateWithSpaces() {
    assertThrows(
        IllegalArgumentException.class, () -> OrderBy.Asc("name").withCollate("unicode ci"));
  }

  @Test
  void formatThenParse_shouldReturnEquivalentObject() {
    OrderBy original = OrderBy.Desc("name").withCollate("unicode").nullsLast();

    String formatted = original.format();
    OrderBy parsed = OrderBy.parse(formatted);

    assertEquals(original, parsed);
  }

  @Test
  void parseThenFormat_shouldReturnEquivalentString() {
    String original = "name,DESC,collate unicode,nulls last";

    OrderBy parsed = OrderBy.parse(original);
    String formatted = parsed.format();

    // Note: This might not be exactly equal due to whitespace handling
    OrderBy reparsed = OrderBy.parse(formatted);
    assertEquals(parsed, reparsed);
  }
}
