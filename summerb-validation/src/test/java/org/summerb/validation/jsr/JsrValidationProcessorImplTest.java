package org.summerb.validation.jsr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import javax.validation.constraints.AssertTrue;

class JsrValidationProcessorImplTest {

  @Test
  void test_expectAnnotationsCanBeCached() throws Exception {

    Field field1 = JsrRow.class.getDeclaredField("booleanValue");
    AssertTrue ann1 = field1.getAnnotation(AssertTrue.class);

    Field field2 = JsrRow.class.getDeclaredField("booleanValue");
    AssertTrue ann2 = field2.getAnnotation(AssertTrue.class);

    Field field3 = JsrRow.class.getDeclaredField("booleanValue2");
    AssertTrue ann3 = field3.getAnnotation(AssertTrue.class);

    assertTrue(ann1 == ann2);
    // NOTE: we can rely only on reference equality as equals() will compare them by-field basis
    assertFalse(ann1 == ann3);
  }
}
