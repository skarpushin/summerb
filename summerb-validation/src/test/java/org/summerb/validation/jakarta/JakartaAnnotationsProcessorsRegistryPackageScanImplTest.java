package org.summerb.validation.jakarta;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.junit.jupiter.api.Test;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.LengthMustBeGreater;
import org.summerb.validation.testDtos.JakartaBean;

class JakartaAnnotationsProcessorsRegistryPackageScanImplTest {

  @Test
  void test_constructor_expectSomethingFound() {
    var f = new JakartaAnnotationsProcessorsRegistryPackageScanImpl();
    Set<Class<? extends Annotation>> supportedAnnotations = f.getSupportedAnnotations();
    assertFalse(supportedAnnotations.isEmpty());
    assertTrue(supportedAnnotations.contains(FutureOrPresent.class));
  }

  @Test
  void test_build_and_use() throws Exception {
    var f = new JakartaAnnotationsProcessorsRegistryPackageScanImpl();
    Annotation annotation = JakartaBean.class.getMethod("getString2").getAnnotations()[0];
    AnnotationProcessor<Annotation> processor =
        f.buildAnnotationProcessor(annotation, "getString2");

    assertNotNull(processor);

    var ctx = new ValidationContext<>();
    processor.validate("a", ctx);
    assertFalse(ctx.isHasErrors());

    processor.validate("", ctx);
    assertEquals(1, ctx.getErrors().size());
    assertNotNull(ctx.findErrorOfTypeForField(LengthMustBeGreater.class, "getString2"));
  }

  @Test
  void test_build_expectErrorOnIrrelevantAnnotation() throws Exception {
    var f = new JakartaAnnotationsProcessorsRegistryPackageScanImpl();
    Annotation annotation = JakartaBean.class.getMethod("getString4").getAnnotations()[0];
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> f.buildAnnotationProcessor(annotation, "getString4"));
    assertTrue(ex.getMessage().startsWith("processorClass not found for annotation:"));
  }

  @Test
  void test_build_expectErrorOnInsantiation() throws Exception {
    var f =
        new JakartaAnnotationsProcessorsRegistryPackageScanImpl(
            "org.summerb.validation.jakarta.test_data");
    Annotation annotation =
        JakartaBean.class.getMethod("setString3", String.class).getAnnotations()[0];
    assertThrows(
        RuntimeException.class, () -> f.buildAnnotationProcessor(annotation, "getString3"));
  }

  @Test
  void test_expectIrrelevantClassesWillBeIgnored() {
    var f =
        new JakartaAnnotationsProcessorsRegistryPackageScanImpl(
            "org.summerb.validation.jakarta.test_data");
    assertEquals(4, f.mapping.size());
    assertTrue(f.getSupportedAnnotations().contains(AssertTrue.class));
    assertTrue(f.getSupportedAnnotations().contains(AssertFalse.class));
    assertTrue(f.getSupportedAnnotations().contains(NotNull.class));
    assertTrue(f.getSupportedAnnotations().contains(Pattern.class));
  }

  /**
   * It would be actually super nice to have this implemented, but I did not found how to traverse
   * ParametrizedType hierarchy and extract parameter types from there. So although cases in these
   * packages are fully valid from Java perspective, our impl cannot correctly handle them. See
   * {@link JakartaAnnotationsProcessorsRegistryPackageScanImpl#findParentTypesToCheck(Class, Set)}
   */
  @Test
  void test_expect_unhandledCasesRecognized_viaGenericSuperInterface() {
    assertThrows(
        RuntimeException.class,
        () ->
            new JakartaAnnotationsProcessorsRegistryPackageScanImpl(
                "org.summerb.validation.jakarta.test_data2"));
  }

  /**
   * It would be actually super nice to have this implemented, but I did not found how to traverse
   * ParametrizedType hierarchy and extract parameter types from there. So although cases in these
   * packages are fully valid from Java perspective, our impl cannot correctly handle them. See
   * {@link JakartaAnnotationsProcessorsRegistryPackageScanImpl#findParentTypesToCheck(Class, Set)}
   */
  @Test
  void test_expect_unhandledCasesRecognized_viaGenericSuperClass() {
    assertThrows(
        RuntimeException.class,
        () ->
            new JakartaAnnotationsProcessorsRegistryPackageScanImpl(
                "org.summerb.validation.jakarta.test_data3"));
  }

  @Test
  void test_constructor_expectException() {
    assertThrows(
        RuntimeException.class,
        () ->
            new JakartaAnnotationsProcessorsRegistryPackageScanImpl() {
              @Override
              protected Set<Class<? extends AnnotationProcessor<? extends Annotation>>>
                  findAnnotationProcessorsClasses(String packageName) {
                throw new IllegalStateException("test");
              }
            });
  }
}
