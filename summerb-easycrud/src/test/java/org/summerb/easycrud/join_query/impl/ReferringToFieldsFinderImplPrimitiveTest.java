package org.summerb.easycrud.join_query.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.row.HasId;

class ReferringToFieldsFinderImplPrimitiveTest {

  private ReferringToFieldsFinderImpl finder = new ReferringToFieldsFinderImpl();

  // Test classes for primitive type scenarios
  static class TestToRowWithPrimitiveId implements HasId<Long> {
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

  static class TestToRowWithIntId implements HasId<Integer> {
    private Integer id;

    @Override
    public Integer getId() {
      return id;
    }

    @Override
    public void setId(Integer id) {
      this.id = id;
    }
  }

  static class TestFromRowWithPrimitiveLong {
    @ReferringTo(TestToRowWithPrimitiveId.class)
    private long primitiveLongField; // Should be compatible with Long
  }

  static class TestFromRowWithPrimitiveInt {
    @ReferringTo(TestToRowWithIntId.class)
    private int primitiveIntField; // Should be compatible with Integer
  }

  static class TestFromRowWithBoxedTypes {
    @ReferringTo(TestToRowWithPrimitiveId.class)
    private Long boxedLongField;

    @ReferringTo(TestToRowWithIntId.class)
    private Integer boxedIntField;
  }

  @Test
  @DisplayName("Should handle primitive long to Long compatibility")
  void shouldHandlePrimitiveLongToLongCompatibility() {
    String result =
        finder.findReferringField(
            TestFromRowWithPrimitiveLong.class, TestToRowWithPrimitiveId.class);
    assertEquals("primitiveLongField", result);
  }

  @Test
  @DisplayName("Should handle primitive int to Integer compatibility")
  void shouldHandlePrimitiveIntToIntegerCompatibility() {
    String result =
        finder.findReferringField(TestFromRowWithPrimitiveInt.class, TestToRowWithIntId.class);
    assertEquals("primitiveIntField", result);
  }

  @Test
  @DisplayName("Should handle boxed type compatibility")
  void shouldHandleBoxedTypeCompatibility() {
    String result1 =
        finder.findReferringField(TestFromRowWithBoxedTypes.class, TestToRowWithPrimitiveId.class);
    String result2 =
        finder.findReferringField(TestFromRowWithBoxedTypes.class, TestToRowWithIntId.class);

    assertEquals("boxedLongField", result1);
    assertEquals("boxedIntField", result2);
  }
}
