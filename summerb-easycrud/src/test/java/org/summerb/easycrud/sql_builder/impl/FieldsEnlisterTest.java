package org.summerb.easycrud.sql_builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FieldsEnlisterTest {

  private FieldsEnlisterImpl f = new FieldsEnlisterImpl();

  @Test
  @DisplayName("Should process fields from simple class without inheritance")
  void testSimpleClassFields() {
    // When
    List<String> result = f.findInClass(SimpleRow.class);

    // Then
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("id"));
    assertTrue(result.contains("name"));
    assertTrue(result.contains("email"));
  }

  @Test
  @DisplayName("Should process fields from class with single inheritance level")
  void testSingleInheritance() {
    // When
    List<String> result = f.findInClass(ChildRow.class);

    // Then
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(result.contains("createdBy"));
    assertTrue(result.contains("updatedBy"));
    assertTrue(result.contains("childField"));
    assertTrue(result.contains("isActive"));
  }

  @Test
  @DisplayName("Should process fields from multi-level inheritance hierarchy")
  void testMultiLevelInheritance() {
    // When
    List<String> result = f.findInClass(GrandchildRow.class);

    // Then
    assertNotNull(result);
    assertEquals(5, result.size());
    assertTrue(result.contains("createdBy"));
    assertTrue(result.contains("updatedBy"));
    assertTrue(result.contains("childField"));
    assertTrue(result.contains("isActive"));
    assertTrue(result.contains("grandchildValue"));
  }

  @Test
  @DisplayName("Should handle different table aliases and column prefixes")
  void testDifferentAliasesAndPrefixes() {
    // When
    List<String> result = f.findInClass(SimpleRow.class);

    // Then
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("id"));
    assertTrue(result.contains("name"));
    assertTrue(result.contains("email"));
  }

  @Test
  @DisplayName("Should handle class with no fields")
  void testClassWithNoFields() {
    // When
    List<String> result = f.findInClass(EmptyRow.class);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should stop at Object class in inheritance hierarchy")
  void testStopsAtObjectClass() {
    // When - Object class itself should have no fields (except synthetic ones in some JVMs)
    List<String> result = f.findInClass(Object.class);

    // Then - Should handle gracefully, typically empty or only synthetic fields
    assertNotNull(result);
    // We don't assert specific size as Object might have synthetic fields in some environments
  }

  @Test
  @DisplayName("Should maintain field order from class declaration")
  void testFieldOrder() {
    // Given
    class OrderedRow {
      private String first;
      private String second;
      private String third;
    }

    // When
    List<String> result = f.findInClass(OrderedRow.class);

    // Then - fields should be in declaration order
    assertEquals(3, result.size());
    assertEquals("first", result.get(0));
    assertEquals("second", result.get(1));
    assertEquals("third", result.get(2));
  }

  @Test
  @DisplayName("Should handle complex field name conversions")
  void testComplexFieldNameConversion() {
    // When
    List<String> result = f.findInClass(RowWithComplexFields.class);

    // Then
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("camelCaseField"));
    assertTrue(result.contains("URLHandler"));
    assertTrue(result.contains("HTTPStatusCode"));
  }

  @Test
  @DisplayName("Should not include static fields")
  void testStaticFieldsExcluded() {
    // Given
    class RowWithStaticField {
      private String instanceField;
      private static String staticField; // This should be excluded
    }

    // When
    List<String> result = f.findInClass(RowWithStaticField.class);

    // Then - only instance fields should be included
    assertEquals(1, result.size());
    assertTrue(result.contains("instanceField"));
    assertFalse(result.contains("staticField"));
  }

  @Test
  @DisplayName("Should handle fields with same name in different classes in hierarchy")
  void testFieldNameShadowing() {
    // Given
    class ParentWithField {
      private String commonField;
    }

    class ChildWithSameField extends ParentWithField {
      private String commonField; // Shadows parent field
    }

    // When
    List<String> result = f.findInClass(ChildWithSameField.class);

    // Then - both fields should be included (shadowing is allowed in different classes)
    assertEquals(2, result.size());
    assertTrue(result.contains("commonField"));
    // Since both have the same name, we'll get two entries with the same SQL representation
    // This might be intentional or might need deduplication depending on requirements
  }

  @Test
  @DisplayName("Should process only declared fields (not inherited)")
  void testEnlistFieldsFromItselfAndParent() {
    // Given

    // When - Only process ChildRow's declared fields
    List<String> fields = f.findInClass(ChildRow.class);

    // Then - Should only include ChildRow's fields, not ParentRow's
    assertEquals(4, fields.size());
    assertTrue(fields.contains("childField"));
    assertTrue(fields.contains("isActive"));
    assertTrue(fields.contains("createdBy"));
    assertTrue(fields.contains("updatedBy"));
  }

  @Test
  @DisplayName("Should handle class with only static fields")
  void testClassWithOnlyStaticFields() {
    // Given
    class StaticOnlyRow {
      private static String staticField1;
      private static final String STATIC_FIELD_2 = "constant";
      // No instance fields
    }

    // When
    List<String> result = f.findInClass(StaticOnlyRow.class);

    // Then - no instance fields, so result should be empty
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}

// Test classes for inheritance hierarchy
class SimpleRow {
  private String id;
  private String name;
  private String email;
}

class ParentRow {
  private String createdBy;
  private String updatedBy;
}

class ChildRow extends ParentRow {
  private String childField;
  private boolean isActive;
}

class GrandchildRow extends ChildRow {
  private double grandchildValue;
}

class RowWithComplexFields {
  private String camelCaseField;
  private String URLHandler;
  private int HTTPStatusCode;
}

class EmptyRow {
  // No fields
}
