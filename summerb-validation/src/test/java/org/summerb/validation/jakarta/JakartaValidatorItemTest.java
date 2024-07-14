package org.summerb.validation.jakarta;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JakartaValidatorItemTest {

  @SuppressWarnings("unchecked")
  @Test
  void test_constructor_expectIaeOnNullArgs() {
    String arg1 = "asd";
    Annotation arg2 = mock(Annotation.class);
    AnnotationProcessor<Annotation> arg3 = mock(AnnotationProcessor.class);
    Function<Object, Object> arg4 = mock(Function.class);

    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidatorItem(null, arg2, arg3, arg4));
    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidatorItem(arg1, null, arg3, arg4));
    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidatorItem(arg1, arg2, null, arg4));
    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidatorItem(arg1, arg2, arg3, null));

    var f = new JakartaValidatorItem(arg1, arg2, arg3, arg4);
    assertEquals(arg3, f.getProcessor());
  }
}
