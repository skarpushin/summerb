package org.summerb.validation;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactory;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;
import org.summerb.validation.testDtos.Bean;

class ValidationContextFactoryImplTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  PropertyNameResolverFactory propertyNameResolverFactory =
      new PropertyNameResolverFactoryImpl(methodCapturerProxyClassFactory);

  // NOTE: Other tests are not created here because same functionality is already covered by
  // ValidationContextTest -- so we only add tests that are needed to complete coverage/mutational
  // coverage

  @Test
  void test_ArgsConstructor() {
    assertThrows(
        IllegalArgumentException.class, () -> new ValidationContextFactoryImpl(null, null));
    assertThrows(
        IllegalArgumentException.class, () -> new ValidationContextFactoryImpl(null, null));

    ValidationContext<Bean> ctx =
        new ValidationContextFactoryImpl(propertyNameResolverFactory, null).buildFor(new Bean());

    assertTrue(ctx.isNull(Bean::getString1));
  }

  @Test
  void test_buildFor_ExpectExceptionHandling() {
    var propertyNameObtainerFactoryMock = Mockito.mock(PropertyNameResolverFactory.class);
    doThrow(new IllegalStateException("test"))
        .when(propertyNameObtainerFactoryMock)
        .getResolver(Bean.class);

    ValidationContextFactoryImpl f =
        new ValidationContextFactoryImpl(propertyNameObtainerFactoryMock, null);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> f.buildFor(new Bean()));
    assertTrue(ex.getMessage().startsWith("Failed to build ValidationContext for"));
  }

  @Test
  void test_buildFor() {
    var f = new ValidationContextFactoryImpl(propertyNameResolverFactory, null);

    // check illegal args
    assertThrows(IllegalArgumentException.class, () -> f.buildFor(null));

    // check builder without bean
    ValidationContext<?> result = f.build();
    assertNotNull(result);
    assertNull(result.propertyNameResolver);
    assertNull(result.bean);
    assertThrows(IllegalStateException.class, () -> result.notNull(Object::hashCode));
  }
}
