package org.summerb.validation.jakarta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.summerb.validation.testDtos.JakartaBean;
import org.summerb.validation.testDtos.JakartaBeanInvalid1;
import org.summerb.validation.testDtos.JakartaBeanInvalid2;
import org.summerb.validation.testDtos.JakartaBeanInvalid3;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

class JakartaValidationBeanProcessorImplTest {

  @Test
  void test_constructor() {
    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidationBeanProcessorImpl(null));
    assertThrows(
        IllegalArgumentException.class, () -> new JakartaValidationBeanProcessorCachedImpl(null));
  }

  @Test
  void test_expectValidationsCreated() {
    var f = buildFixture();
    List<JakartaValidatorItem> r = f.getValidationsFor(JakartaBean.class);
    assertEquals(3, r.size());

    assertEquals(Size.class, annotationOnField(r, "string1"));
    assertEquals(NotEmpty.class, annotationOnField(r, "string2"));
    assertEquals(NotNull.class, annotationOnField(r, "string3"));
  }

  @Test
  void test_expectValueGetterWorks() {
    var f = buildFixture();
    List<JakartaValidatorItem> r = f.getValidationsFor(JakartaBean.class);

    JakartaBean bean1 = new JakartaBean();
    bean1.setString1("asd");

    assertEquals("asd", fieldValidator(r, "string1").getValueGetter().apply(bean1));
  }

  @Test
  void test_expectGetterThrowsException() {
    var f = buildFixture();
    List<JakartaValidatorItem> r = f.getValidationsFor(JakartaBeanInvalid3.class);

    JakartaBeanInvalid3 bean1 = new JakartaBeanInvalid3();
    bean1.setThrowsException("asd");

    assertThrows(
        RuntimeException.class,
        () -> fieldValidator(r, "throwsException").getValueGetter().apply(bean1));
  }

  protected JakartaValidatorItem fieldValidator(List<JakartaValidatorItem> r, String name) {
    return r.stream().filter(x -> x.getPropertyName().equals(name)).findFirst().orElse(null);
  }

  protected Class<? extends Annotation> annotationOnField(
      List<JakartaValidatorItem> r, String name) {
    return r.stream()
        .filter(x -> x.getPropertyName().equals(name))
        .map(x -> x.getAnnotation().annotationType())
        .findFirst()
        .orElse(null);
  }

  @Test
  void test_expectIaeForPojoWithoutMatchingGetterSetter() {
    var f = buildFixture();
    assertThrows(
        IllegalArgumentException.class, () -> f.getValidationsFor(JakartaBeanInvalid1.class));
  }

  @Test
  void test_expectIaeForPojoWithDuplicateFields() {
    var f = buildFixture();
    assertThrows(
        IllegalArgumentException.class, () -> f.getValidationsFor(JakartaBeanInvalid2.class));
  }

  @Test
  void test_expectFieldWithoutAnnotationsNotIntrospected() {
    var f =
        new JakartaValidationBeanProcessorImpl(registry()) {
          @Override
          protected PropertyDescriptor findPropertyDescriptorForField(Class<?> clazz, Field field) {
            Preconditions.checkArgument(
                !field.getName().equals("string5"),
                "Method findPropertyDescriptorForField must not be called for field that does not have annotations");
            Preconditions.checkArgument(
                !field.getName().equals("string6"),
                "Method findPropertyDescriptorForField must not be called for field that have only irrelevant annotations");
            return super.findPropertyDescriptorForField(clazz, field);
          }
        };

    f.getValidationsFor(JakartaBean.class);
  }

  protected JakartaValidationBeanProcessorCachedImpl buildFixture() {
    return new JakartaValidationBeanProcessorCachedImpl(
        new JakartaValidationBeanProcessorImpl(registry()));
  }

  protected JakartaAnnotationsProcessorsRegistryPackageScanImpl registry() {
    return new JakartaAnnotationsProcessorsRegistryPackageScanImpl();
  }
}
