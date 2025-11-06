package org.summerb.easycrud.join_query.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.row.HasId;

class ReferringToFieldsFinderImplTest {

  private ReferringToFieldsFinderImpl finder;

  @BeforeEach
  void setUp() {
    finder = new ReferringToFieldsFinderImpl();
  }

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {
    @Test
    @DisplayName("Should create instance successfully")
    void shouldCreateInstance() {
      assertNotNull(new ReferringToFieldsFinderImpl());
    }
  }

  @Nested
  @DisplayName("findReferringField method tests")
  class FindReferringFieldTests {

    @Test
    @DisplayName("Should throw exception when fromRow is null")
    void shouldThrowExceptionWhenFromRowIsNull() {
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> finder.findReferringField(null, TestToRow.class));
      assertTrue(exception.getMessage().contains("fromRow required"));
    }

    @Test
    @DisplayName("Should throw exception when toRow is null")
    void shouldThrowExceptionWhenToRowIsNull() {
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> finder.findReferringField(TestToRow.class, null));
      assertTrue(exception.getMessage().contains("toRow required"));
    }

    @Test
    @DisplayName("Should return null when no annotated fields found")
    void shouldReturnNullWhenNoAnnotatedFieldsFound() {
      String result = finder.findReferringField(TestFromRowNoAnnotations.class, TestToRow.class);
      assertNull(result);
    }

    @Test
    @DisplayName("Should return field name when exactly one valid annotated field exists")
    void shouldReturnFieldNameWhenOneValidAnnotatedFieldExists() {
      String result = finder.findReferringField(TestFromRowSingleValid.class, TestToRow.class);
      assertEquals("validField", result);
    }

    @Test
    @DisplayName("Should return null and log warning when no fields have compatible types")
    void shouldReturnNullAndLogWarningWhenNoCompatibleTypes() {
      try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
        Logger mockLogger = mock(Logger.class);
        loggerFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);

        ReferringToFieldsFinderImpl localFinder = new ReferringToFieldsFinderImpl();

        String result =
            localFinder.findReferringField(TestFromRowIncompatibleType.class, TestToRow.class);

        assertNull(result);
        verify(mockLogger)
            .warn(
                eq(
                    "Found {} field(s) with @ReferringTo({}) in {}, but none have compatible ID type (expected: {})"),
                eq(1),
                eq("TestToRow"),
                eq("TestFromRowIncompatibleType"),
                eq("Long"));
      }
    }

    @Test
    @DisplayName("Should return null and log warning when multiple valid fields exist")
    void shouldReturnNullAndLogWarningWhenMultipleValidFieldsExist() {
      try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
        Logger mockLogger = mock(Logger.class);
        loggerFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);

        ReferringToFieldsFinderImpl localFinder = new ReferringToFieldsFinderImpl();

        String result =
            localFinder.findReferringField(TestFromRowMultipleValid.class, TestToRow.class);

        assertNull(result);
        verify(mockLogger)
            .warn(
                eq(
                    "Found {} (more than 1) fields with @ReferringTo({}) and compatible ID type in {}: {}"),
                eq(2),
                eq("TestToRow"),
                eq("TestFromRowMultipleValid"),
                any(List.class));
      }
    }
  }

  @Nested
  @DisplayName("getRowIdType method tests")
  class GetRowIdTypeTests {

    @Test
    @DisplayName("Should return correct ID type for class implementing HasId")
    void shouldReturnCorrectIdType() {
      Class<?> idType = finder.getRowIdType(TestToRow.class);
      assertEquals(Long.class, idType);
    }

    @Test
    @DisplayName("Should throw exception when class does not implement HasId")
    void shouldThrowExceptionWhenClassDoesNotImplementHasId() {
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> finder.getRowIdType(TestClassWithoutHasId.class));
      assertTrue(exception.getMessage().contains("does not implement HasId"));
    }
  }

  @Nested
  @DisplayName("isTypeCompatible method tests")
  class IsTypeCompatibleTests {

    @Test
    @DisplayName("Should return true for identical types")
    void shouldReturnTrueForIdenticalTypes() {
      assertTrue(finder.isTypeCompatible(String.class, String.class));
      assertTrue(finder.isTypeCompatible(Long.class, Long.class));
    }

    @Test
    @DisplayName("Should return true for primitive-boxed type pairs")
    void shouldReturnTrueForPrimitiveBoxedPairs() {
      assertTrue(finder.isTypeCompatible(long.class, Long.class));
      assertTrue(finder.isTypeCompatible(int.class, Integer.class));
      assertTrue(finder.isTypeCompatible(byte.class, Byte.class));
      assertTrue(finder.isTypeCompatible(char.class, Character.class));
      assertTrue(finder.isTypeCompatible(short.class, Short.class));

      // Test reverse direction
      assertTrue(finder.isTypeCompatible(Long.class, long.class));
      assertTrue(finder.isTypeCompatible(Integer.class, int.class));
    }

    @Test
    @DisplayName("Should return false for incompatible types")
    void shouldReturnFalseForIncompatibleTypes() {
      assertFalse(finder.isTypeCompatible(String.class, Long.class));
      assertFalse(finder.isTypeCompatible(Long.class, String.class));
      assertFalse(finder.isTypeCompatible(int.class, Long.class)); // Different primitive/boxed
      assertFalse(finder.isTypeCompatible(double.class, Double.class)); // Not in supported pairs
    }
  }

  @Nested
  @DisplayName("findMatchingAnnotatedFields method tests")
  class FindMatchingAnnotatedFieldsTests {

    @Test
    @DisplayName("Should find annotated fields in current class")
    void shouldFindAnnotatedFieldsInCurrentClass() {
      List<Field> fields =
          ReferringToFieldsFinderImpl.findMatchingAnnotatedFields(
              TestFromRowSingleValid.class, TestToRow.class);

      assertEquals(1, fields.size());
      assertEquals("validField", fields.get(0).getName());
    }

    @Test
    @DisplayName("Should find annotated fields in superclass")
    void shouldFindAnnotatedFieldsInSuperclass() {
      List<Field> fields =
          ReferringToFieldsFinderImpl.findMatchingAnnotatedFields(
              TestFromRowChild.class, TestToRow.class);

      // Should find both parent and child fields
      assertEquals(2, fields.size());
    }

    @Test
    @DisplayName("Should return empty list when no matching annotations found")
    void shouldReturnEmptyListWhenNoMatchingAnnotationsFound() {
      List<Field> fields =
          ReferringToFieldsFinderImpl.findMatchingAnnotatedFields(
              TestFromRowNoAnnotations.class, TestToRow.class);

      assertTrue(fields.isEmpty());
    }

    @Test
    @DisplayName("Should only return fields with matching annotation value")
    void shouldOnlyReturnFieldsWithMatchingAnnotationValue() {
      List<Field> fields =
          ReferringToFieldsFinderImpl.findMatchingAnnotatedFields(
              TestFromRowDifferentAnnotation.class, TestToRow.class);

      // Should only find field annotated with TestToRow, not TestOtherToRow
      assertEquals(1, fields.size());
      assertEquals("correctField", fields.get(0).getName());
    }
  }

  @Nested
  @DisplayName("findFieldsOfValidType method tests")
  class FindFieldsOfValidTypeTests {

    @Test
    @DisplayName("Should filter fields by compatible types")
    void shouldFilterFieldsByCompatibleTypes() throws NoSuchFieldException {
      List<Field> candidateFields =
          List.of(
              TestFromRowMultipleValid.class.getDeclaredField("validField1"),
              TestFromRowMultipleValid.class.getDeclaredField("validField2"),
              TestFromRowMultipleValid.class.getDeclaredField("incompatibleField"));

      List<Field> validFields = finder.findFieldsOfValidType(candidateFields, Long.class);

      assertEquals(2, validFields.size());
      assertTrue(
          validFields.stream()
              .allMatch(
                  field ->
                      field.getName().equals("validField1")
                          || field.getName().equals("validField2")));
    }

    @Test
    @DisplayName("Should return empty list when no fields have compatible types")
    void shouldReturnEmptyListWhenNoCompatibleTypes() throws NoSuchFieldException {
      List<Field> candidateFields =
          List.of(TestFromRowIncompatibleType.class.getDeclaredField("incompatibleField"));

      List<Field> validFields = finder.findFieldsOfValidType(candidateFields, Long.class);

      assertTrue(validFields.isEmpty());
    }
  }

  // Test classes for various scenarios

  // Basic test classes
  static class TestToRow implements HasId<Long> {
    private Long id;

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public void setId(Long id) {
      this.id = id;
    }
  }

  static class TestOtherToRow implements HasId<String> {
    private String id;

    @Override
    public String getId() {
      return id;
    }

    @Override
    public void setId(String id) {
      this.id = id;
    }
  }

  // Test classes for different scenarios
  static class TestFromRowNoAnnotations {
    private String someField;
  }

  static class TestFromRowSingleValid {
    @ReferringTo(TestToRow.class)
    private Long validField;

    private String otherField;
  }

  static class TestFromRowIncompatibleType {
    @ReferringTo(TestToRow.class)
    private String incompatibleField; // String is incompatible with Long
  }

  static class TestFromRowMultipleValid {
    @ReferringTo(TestToRow.class)
    private Long validField1;

    @ReferringTo(TestToRow.class)
    private Long validField2;

    @ReferringTo(TestToRow.class)
    private String incompatibleField;
  }

  static class TestFromRowDifferentAnnotation {
    @ReferringTo(TestToRow.class)
    private Long correctField;

    @ReferringTo(TestOtherToRow.class)
    private String wrongAnnotationField;
  }

  // Inheritance test classes
  static class TestFromRowParent {
    @ReferringTo(TestToRow.class)
    protected Long parentField;
  }

  static class TestFromRowChild extends TestFromRowParent {
    @ReferringTo(TestToRow.class)
    private Long childField;
  }

  // Negative test classes
  static class TestClassWithoutHasId {
    // Does not implement HasId
  }
}
