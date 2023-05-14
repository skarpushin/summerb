package org.summerb.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactory;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameObtainer;
import org.summerb.methodCapturers.PropertyNameObtainerFactory;
import org.summerb.methodCapturers.PropertyNameObtainerFactoryImpl;
import org.summerb.utils.clock.NowResolverImpl;
import org.summerb.validation.errors.LengthMustBeBetween;
import org.summerb.validation.errors.LengthMustBeGreater;
import org.summerb.validation.errors.LengthMustBeGreaterOrEqual;
import org.summerb.validation.errors.LengthMustBeLess;
import org.summerb.validation.errors.LengthMustBeLessOrEqual;
import org.summerb.validation.errors.LengthMustNotBeBetween;
import org.summerb.validation.errors.MustBeBetween;
import org.summerb.validation.errors.MustBeEmpty;
import org.summerb.validation.errors.MustBeEqualTo;
import org.summerb.validation.errors.MustBeFalse;
import org.summerb.validation.errors.MustBeGreater;
import org.summerb.validation.errors.MustBeGreaterOrEqual;
import org.summerb.validation.errors.MustBeIn;
import org.summerb.validation.errors.MustBeInFuture;
import org.summerb.validation.errors.MustBeInFutureOrPresent;
import org.summerb.validation.errors.MustBeInPast;
import org.summerb.validation.errors.MustBeInPastOrPresent;
import org.summerb.validation.errors.MustBeLess;
import org.summerb.validation.errors.MustBeLessOrEqual;
import org.summerb.validation.errors.MustBeNull;
import org.summerb.validation.errors.MustBeTrue;
import org.summerb.validation.errors.MustBeValidEmail;
import org.summerb.validation.errors.MustContain;
import org.summerb.validation.errors.MustEndWith;
import org.summerb.validation.errors.MustHaveText;
import org.summerb.validation.errors.MustMatchPattern;
import org.summerb.validation.errors.MustNotBeBetween;
import org.summerb.validation.errors.MustNotBeEmpty;
import org.summerb.validation.errors.MustNotBeEqualTo;
import org.summerb.validation.errors.MustNotBeIn;
import org.summerb.validation.errors.MustNotBeNull;
import org.summerb.validation.errors.MustNotContain;
import org.summerb.validation.errors.MustNotEndWith;
import org.summerb.validation.errors.MustNotStartWith;
import org.summerb.validation.errors.MustStartWith;
import org.summerb.validation.jakarta.JakartaValidator;
import org.summerb.validation.testDtos.Dated;
import org.summerb.validation.testDtos.Bean;
import org.summerb.validation.testDtos.Beans;

import com.google.common.collect.Range;

class ValidationContextTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  PropertyNameObtainerFactory propertyNameObtainerFactory =
      new PropertyNameObtainerFactoryImpl(methodCapturerProxyClassFactory);

  ValidationContextFactory validationContextFactory =
      new ValidationContextFactoryImpl(propertyNameObtainerFactory, null);

  @Test
  void test_Constructor_ExpectDefaultConstructorWorks() {
    var f = new ValidationContext<>();
    assertFalse(f.isHasErrors());
  }

  @SuppressWarnings("unchecked")
  @Test
  void test_Constructor_ExpectIaeOnNullArgs() {
    ValidationErrors arg1 = new ValidationErrors();
    PropertyNameObtainer<Object> arg2 = Mockito.mock(PropertyNameObtainer.class);
    JakartaValidator arg3 = Mockito.mock(JakartaValidator.class);
    ValidationContextFactory arg4 = validationContextFactory;

    new ValidationContext<>(arg1, arg2, arg3, null); // expect ok
    new ValidationContext<>(arg1, arg2, null, arg4); // expect ok
    assertThrows(
        IllegalArgumentException.class, () -> new ValidationContext<>(arg1, null, arg3, arg4));
    assertThrows(
        IllegalArgumentException.class, () -> new ValidationContext<>(null, arg2, arg3, arg4));
  }

  @Test
  void test_processJakartaValidations_ExpectIse() {
    var f = new ValidationContext<>();
    assertThrows(IllegalStateException.class, () -> f.processJakartaValidations());
  }

  @Test
  void test_IsHasErrors_ExpectTrue() {
    var f = new ValidationContext<>();
    f.add(new ValidationError("pn", "mc", "arg1", 3, "arg3"));
    assertTrue(f.isHasErrors());
  }

  @Test
  void test_GetErrors_ExpectFound() {
    var f = new ValidationContext<>();
    ValidationError error = new ValidationError("pn", "mc", "arg1", 3, "arg3");
    f.add(error);
    assertEquals(error, f.getErrors().get(0));
  }

  @Test
  void test_HasErrorOfType() {
    var f = new ValidationContext<>();
    f.add(new MustNotBeNull("pn"));
    assertTrue(f.hasErrorOfType(MustNotBeNull.class));
    assertFalse(f.hasErrorOfType(MustBeValidEmail.class));
  }

  @Test
  void test_FindErrorOfType() {
    var f = new ValidationContext<>();
    MustNotBeNull error = new MustNotBeNull("pn");
    f.add(error);

    assertNull(f.findErrorOfType(MustBeValidEmail.class));
    assertEquals(error, f.findErrorOfType(MustNotBeNull.class));
  }

  @Test
  void test_FindErrorsForField() {
    var f = new ValidationContext<>();
    MustNotBeNull f1e1 = new MustNotBeNull("pn1");
    f.add(f1e1);
    MustBeValidEmail f1e2 = new MustBeValidEmail("pn1");
    f.add(f1e2);
    MustNotBeNull f2e1 = new MustNotBeNull("pn2");
    f.add(f2e1);

    List<ValidationError> pn1Errors = f.findErrorsForField("pn1");
    assertEquals(2, pn1Errors.size());
    assertEquals(f1e1, pn1Errors.get(0));
    assertEquals(f1e2, pn1Errors.get(1));

    List<ValidationError> pn2Errors = f.findErrorsForField("pn2");
    assertEquals(1, pn2Errors.size());
    assertEquals(f2e1, pn2Errors.get(0));
  }

  @Test
  void test_FindErrorOfTypeForField() {
    var f = new ValidationContext<>();
    MustNotBeNull f1e1 = new MustNotBeNull("pn1");
    f.add(f1e1);
    MustBeValidEmail f1e2 = new MustBeValidEmail("pn1");
    f.add(f1e2);
    MustNotBeNull f2e1 = new MustNotBeNull("pn2");
    f.add(f2e1);

    MustNotBeNull error = f.findErrorOfTypeForField(MustNotBeNull.class, "pn2");
    assertEquals(f2e1, error);
  }

  @SuppressWarnings("deprecation")
  @Test
  void test_Add_ExpectIaeOnInvalidError() {
    var f = new ValidationContext<>();
    assertThrows(IllegalArgumentException.class, () -> f.add(null));

    ValidationError ve1 = new ValidationError("asd", "asd");
    assertThrows(IllegalArgumentException.class, () -> ve1.setMessageCode(null));
    assertThrows(IllegalArgumentException.class, () -> f.add(new ValidationError()));

    ValidationError ve1a = new ValidationError("asd", "asd");
    assertThrows(IllegalArgumentException.class, () -> ve1a.setMessageCode(" "));

    ValidationError ve2 = new ValidationError("asd", "asd");
    assertThrows(IllegalArgumentException.class, () -> ve2.setPropertyName(null));

    ValidationError ve2a = new ValidationError("asd", "asd");
    assertThrows(IllegalArgumentException.class, () -> ve2a.setPropertyName(" "));

    ValidationError ve3 = new ValidationError();
    ve3.setMessageCode("mc");
    assertThrows(IllegalArgumentException.class, () -> f.add(ve3));

    ValidationError ve4 = new ValidationError();
    ve4.setPropertyName("pn");
    assertThrows(IllegalArgumentException.class, () -> f.add(ve4));
  }

  @Test
  void test_Add_ExpectDuplicateErrorWillNotBeAdded() {
    var f = new ValidationContext<>();
    f.add(new ValidationError("pn", "mc"));
    f.add(new ValidationError("pn", "mc1"));
    f.add(new ValidationError("pn1", "mc"));
    f.add(new ValidationError("pn", "mc"));
    assertEquals(3, f.getErrors().size());

    assertEquals(
        "mc,mc1",
        f.findErrorsForField("pn").stream()
            .map(x -> x.getMessageCode())
            .collect(Collectors.joining(",")));
    assertEquals(
        "mc",
        f.findErrorsForField("pn1").stream()
            .map(x -> x.getMessageCode())
            .collect(Collectors.joining(",")));
  }

  @Test
  void test_isAlreadyHasSameErrorForSameField_ExpectIaeOnInvalidInput() {
    var f = new ValidationContext<>();
    assertThrows(IllegalArgumentException.class, () -> f.isAlreadyHasSameErrorForSameField(null));
  }

  @Test
  void test_ThrowIfHasErrors() {
    var f = new ValidationContext<>();
    f.throwIfHasErrors();
    f.add(new MustNotBeNull("pn1"));
    assertThrows(ValidationException.class, () -> f.throwIfHasErrors());
  }

  @Test
  void test_GetPropertyName_ExpectIaeOnInvalidInput() {
    var f = new ValidationContext<ValidationErrors>();
    assertThrows(IllegalStateException.class, () -> f.getPropertyName(ValidationErrors::getList));

    ValidationContext<ValidationErrors> f2 =
        validationContextFactory.buildFor(new ValidationErrors());
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> f2.getPropertyName(null));
    assertEquals("getPropertyName: getter required", ex.getMessage());
  }

  @Test
  void test_GetPropertyName_ExpectCorrectResponse() {
    ValidationContext<ValidationErrors> f =
        validationContextFactory.buildFor(new ValidationErrors());
    assertEquals("list", f.getPropertyName(ValidationErrors::getList));
  }

  @Test
  void test_GetValue_ExpectCorrectResponse() {
    ValidationContext<ValidationError> f =
        validationContextFactory.buildFor(new ValidationError("pn", "mc"));
    assertEquals("pn", f.getValue(ValidationError::getPropertyName));
    assertEquals("mc", f.getValue(ValidationError::getMessageCode));
  }

  @Test
  void test_GetValue_ExpectIaeOnInvalidInput() {
    var f = new ValidationContext<ValidationError>();
    assertThrows(IllegalStateException.class, () -> f.getValue(ValidationError::getMessageCode));

    ValidationContext<ValidationError> f2 =
        validationContextFactory.buildFor(new ValidationError("pn", "mc"));
    assertThrows(IllegalArgumentException.class, () -> f2.getValue(null));
  }

  @Test
  void test_Null() {
    var f = validationContextFactory.buildFor(new ValidationError("pn", "mc"));
    assertTrue(f.isNull(ValidationError::getMessageArgs));
    assertTrue(f.notNull(ValidationError::getMessageCode));
    assertEquals(0, f.getErrors().size());

    assertFalse(f.isNull(ValidationError::getMessageCode));
    assertFalse(f.notNull(ValidationError::getMessageArgs));
    assertEquals(2, f.getErrors().size());

    assertNotNull(f.findErrorOfTypeForField(MustBeNull.class, ValidationError::getMessageCode));
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, ValidationError::getMessageArgs));
  }

  @Test
  void test_True() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);
    assertTrue(f.isFalse(Bean::isbValue1));
    assertFalse(f.isTrue(Bean::isbValue1));
    assertEquals(1, f.getErrors().size());
    assertNotNull(f.findErrorOfTypeForField(MustBeTrue.class, Bean::isbValue1));

    bean = new Bean();
    bean.setbValue1(true);
    f = validationContextFactory.buildFor(bean);
    assertTrue(f.isTrue(Bean::isbValue1));
    assertFalse(f.isFalse(Bean::isbValue1));
    assertEquals(1, f.getErrors().size());
    assertNotNull(f.findErrorOfTypeForField(MustBeFalse.class, Bean::isbValue1));
  }

  @Test
  void test_True_ExpectNullsWIllBeTreadedAsWrongValue() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);
    assertFalse(f.isTrue(Bean::getbValue2));
    assertEquals(1, f.getErrors().size());
    assertNotNull(f.findErrorOfTypeForField(MustBeTrue.class, Bean::getbValue2));

    bean = new Bean();
    bean.setbValue1(true);
    f = validationContextFactory.buildFor(bean);
    assertFalse(f.isFalse(Bean::getbValue2));
    assertEquals(1, f.getErrors().size());
    assertNotNull(f.findErrorOfTypeForField(MustBeFalse.class, Bean::getbValue2));
  }

  @Test
  void test_Eq() {
    var f = validationContextFactory.buildFor(new ValidationError("pn", "mc"));
    assertTrue(f.eq(ValidationError::getPropertyName, "pn"));
    assertTrue(f.ne(ValidationError::getPropertyName, "zz"));

    assertFalse(f.eq(ValidationError::getPropertyName, "pn1"));
    assertFalse(f.ne(ValidationError::getPropertyName, "pn"));

    assertEquals(
        "pn1",
        f.findErrorOfTypeForField(MustBeEqualTo.class, ValidationError::getPropertyName)
            .getMessageArgs()[0]);
    assertEquals(
        "pn",
        f.findErrorOfTypeForField(MustNotBeEqualTo.class, ValidationError::getPropertyName)
            .getMessageArgs()[0]);
  }

  @Test
  void test_Less() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // invalid boundary
    assertThrows(IllegalArgumentException.class, () -> f.less(Bean::getiValue1, null));

    // null value
    assertFalse(f.less(Bean::getiValue2, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // edge case - no good
    f.errors.clear();
    assertFalse(f.less(Bean::getiValue1, 50));
    assertEquals(1, f.errors.size());
    assertEquals(
        50, f.findErrorOfTypeForField(MustBeLess.class, Bean::getiValue1).getMessageArgs()[0]);

    // edge case - good
    f.errors.clear();
    assertTrue(f.less(Bean::getiValue1, 51));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_LessOrEqual() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // invalid boundary
    assertThrows(IllegalArgumentException.class, () -> f.le(Bean::getiValue1, null));

    // null value
    assertFalse(f.le(Bean::getiValue2, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // edge case - no good
    f.errors.clear();
    assertFalse(f.le(Bean::getiValue1, 49));
    assertEquals(1, f.errors.size());
    assertEquals(
        49,
        f.findErrorOfTypeForField(MustBeLessOrEqual.class, Bean::getiValue1).getMessageArgs()[0]);

    // edge case - good => equals
    f.errors.clear();
    assertTrue(f.le(Bean::getiValue1, 50));
    assertEquals(0, f.errors.size());

    // edge case - good => less
    f.errors.clear();
    assertTrue(f.le(Bean::getiValue1, 51));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_Greter() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // invalid boundary
    assertThrows(IllegalArgumentException.class, () -> f.greater(Bean::getiValue1, null));

    // null value
    assertFalse(f.greater(Bean::getiValue2, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // edge case - no good
    f.errors.clear();
    assertFalse(f.greater(Bean::getiValue1, 50));
    assertEquals(1, f.errors.size());
    assertEquals(
        50, f.findErrorOfTypeForField(MustBeGreater.class, Bean::getiValue1).getMessageArgs()[0]);

    // edge case - good
    f.errors.clear();
    assertTrue(f.greater(Bean::getiValue1, 49));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_GreaterOrEqual() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal boundary
    assertThrows(IllegalArgumentException.class, () -> f.ge(Bean::getiValue1, null));

    // null value
    assertFalse(f.ge(Bean::getiValue2, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // edge case - no good
    f.errors.clear();
    assertFalse(f.ge(Bean::getiValue1, 51));
    assertEquals(1, f.errors.size());
    assertEquals(
        51,
        f.findErrorOfTypeForField(MustBeGreaterOrEqual.class, Bean::getiValue1).getMessageArgs()[0]);

    // edge case - good => equals
    f.errors.clear();
    assertTrue(f.ge(Bean::getiValue1, 50));
    assertEquals(0, f.errors.size());

    // edge case - good => less
    f.errors.clear();
    assertTrue(f.ge(Bean::getiValue1, 49));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_In() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.in(Bean::getiValue1, null));
    assertThrows(IllegalArgumentException.class, () -> f.in(Bean::getiValue1, new LinkedList<>()));

    // valid case
    assertTrue(f.in(Bean::getiValue1, Arrays.asList(49, 50, 51)));

    // invalid case
    assertFalse(f.in(Bean::getiValue1, Arrays.asList(51, 52)));
    assertEquals(1, f.errors.size());
    assertEquals(
        "[51, 52]", f.findErrorOfTypeForField(MustBeIn.class, Bean::getiValue1).getMessageArgs()[0]);
  }

  @Test
  void test_NotIn() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notIn(Bean::getiValue1, null));
    assertThrows(
        IllegalArgumentException.class, () -> f.notIn(Bean::getiValue1, new LinkedList<>()));

    // valid case
    assertTrue(f.notIn(Bean::getiValue1, Arrays.asList(49, 51)));

    // invalid case
    assertFalse(f.notIn(Bean::getiValue1, Arrays.asList(50, 51)));
    assertEquals(1, f.errors.size());
    assertEquals(
        "[50, 51]",
        f.findErrorOfTypeForField(MustNotBeIn.class, Bean::getiValue1).getMessageArgs()[0]);
  }

  @Test
  void test_Between() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.between(Bean::getiValue1, null, 100));
    assertThrows(IllegalArgumentException.class, () -> f.between(Bean::getiValue1, 100, null));
    assertThrows(IllegalArgumentException.class, () -> f.between(Bean::getiValue1, 100, 50));
    assertThrows(IllegalArgumentException.class, () -> f.between(Bean::getiValue1, 50, 50));

    // null value
    assertFalse(f.between(Bean::getiValue2, 25, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, 25, 49));
    assertEquals(1, f.errors.size());
    MustBeBetween ve = f.findErrorOfTypeForField(MustBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("[", ve.getMessageArgs()[0]);
    assertEquals(25, ve.getMessageArgs()[1]);
    assertEquals(49, ve.getMessageArgs()[2]);
    assertEquals("]", ve.getMessageArgs()[3]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, 51, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(ve);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.between(Bean::getiValue1, 25, 50));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.between(Bean::getiValue1, 50, 100));
    assertEquals(0, f.errors.size());

    // valid - middle
    assertTrue(f.between(Bean::getiValue1, 25, 100));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_NotBetween() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, null, 100));
    assertThrows(IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, 100, null));
    assertThrows(IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, 100, 50));
    assertThrows(IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, 50, 50));

    // null value
    assertFalse(f.notBetween(Bean::getiValue2, 25, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.notBetween(Bean::getiValue1, 25, 50));
    assertEquals(1, f.errors.size());
    MustNotBeBetween ve = f.findErrorOfTypeForField(MustNotBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("[", ve.getMessageArgs()[0]);
    assertEquals(25, ve.getMessageArgs()[1]);
    assertEquals(50, ve.getMessageArgs()[2]);
    assertEquals("]", ve.getMessageArgs()[3]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.notBetween(Bean::getiValue1, 50, 100));
    assertEquals(1, f.errors.size());
    assertNotNull(ve);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.notBetween(Bean::getiValue1, 25, 49));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.notBetween(Bean::getiValue1, 51, 100));
    assertEquals(0, f.errors.size());

    // valid - middle
    assertTrue(f.notBetween(Bean::getiValue1, 1, 2));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_BeBetweenRange() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.between(Bean::getiValue1, null));
    assertThrows(
        IllegalArgumentException.class, () -> f.between(Bean::getiValue1, Range.greaterThan(1)));
    assertThrows(
        IllegalArgumentException.class, () -> f.between(Bean::getiValue1, Range.lessThan(1)));

    // null value
    assertFalse(f.between(Bean::getiValue2, Range.closed(25, 100)));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, Range.closed(25, 49)));
    assertEquals(1, f.errors.size());
    MustBeBetween ve = f.findErrorOfTypeForField(MustBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("[", ve.getMessageArgs()[0]);
    assertEquals(25, ve.getMessageArgs()[1]);
    assertEquals(49, ve.getMessageArgs()[2]);
    assertEquals("]", ve.getMessageArgs()[3]);

    // invalid - left edge case2
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, Range.closedOpen(25, 50)));
    assertEquals(1, f.errors.size());
    ve = f.findErrorOfTypeForField(MustBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("[", ve.getMessageArgs()[0]);
    assertEquals(25, ve.getMessageArgs()[1]);
    assertEquals(50, ve.getMessageArgs()[2]);
    assertEquals(")", ve.getMessageArgs()[3]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, Range.closed(51, 100)));
    assertEquals(1, f.errors.size());
    assertNotNull(ve);

    // invalid - right edge case2
    f.errors.clear();
    assertFalse(f.between(Bean::getiValue1, Range.openClosed(50, 100)));
    assertEquals(1, f.errors.size());
    ve = f.findErrorOfTypeForField(MustBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("(", ve.getMessageArgs()[0]);
    assertEquals(50, ve.getMessageArgs()[1]);
    assertEquals(100, ve.getMessageArgs()[2]);
    assertEquals("]", ve.getMessageArgs()[3]);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.between(Bean::getiValue1, Range.closed(25, 50)));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.between(Bean::getiValue1, Range.closed(50, 100)));
    assertEquals(0, f.errors.size());

    // valid - middle
    assertTrue(f.between(Bean::getiValue1, Range.closed(25, 100)));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_NotBeBetweenRange() {
    Bean bean = new Bean();
    bean.setiValue1(50);
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, null));
    assertThrows(
        IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, Range.greaterThan(50)));
    assertThrows(
        IllegalArgumentException.class, () -> f.notBetween(Bean::getiValue1, Range.lessThan(50)));

    // null value
    assertFalse(f.notBetween(Bean::getiValue2, Range.closed(25, 100)));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getiValue2));

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.notBetween(Bean::getiValue1, Range.closed(25, 50)));
    assertEquals(1, f.errors.size());
    MustNotBeBetween ve = f.findErrorOfTypeForField(MustNotBeBetween.class, Bean::getiValue1);
    assertNotNull(ve);
    assertEquals("[", ve.getMessageArgs()[0]);
    assertEquals(25, ve.getMessageArgs()[1]);
    assertEquals(50, ve.getMessageArgs()[2]);
    assertEquals("]", ve.getMessageArgs()[3]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.notBetween(Bean::getiValue1, Range.closed(50, 100)));
    assertEquals(1, f.errors.size());
    assertNotNull(ve);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.notBetween(Bean::getiValue1, Range.closed(25, 49)));
    assertEquals(0, f.errors.size());
    // valid - left edge case 2
    assertTrue(f.notBetween(Bean::getiValue1, Range.closedOpen(25, 50)));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.notBetween(Bean::getiValue1, Range.closed(51, 100)));
    assertEquals(0, f.errors.size());
    // valid - right edge case2
    assertTrue(f.notBetween(Bean::getiValue1, Range.openClosed(50, 100)));
    assertEquals(0, f.errors.size());

    // valid - middle
    assertTrue(f.notBetween(Bean::getiValue1, Range.closed(1, 2)));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_LengthMustBeBetween() {
    Bean bean = new Bean();
    bean.setString1("0123456789");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.lengthBetween(Bean::getString2, -1, 100));
    assertThrows(IllegalArgumentException.class, () -> f.lengthBetween(Bean::getString2, 100, -1));
    assertThrows(IllegalArgumentException.class, () -> f.lengthBetween(Bean::getString2, 100, 50));
    assertThrows(IllegalArgumentException.class, () -> f.lengthBetween(Bean::getString2, 50, 50));

    // null value
    assertTrue(f.lengthBetween(Bean::getString2, 0, 10));
    assertEquals(0, f.errors.size());

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.lengthBetween(Bean::getString1, 5, 9));
    assertEquals(1, f.errors.size());
    LengthMustBeBetween ve = f.findErrorOfTypeForField(LengthMustBeBetween.class, Bean::getString1);
    assertNotNull(ve);
    assertEquals(5, ve.getMessageArgs()[0]);
    assertEquals(9, ve.getMessageArgs()[1]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.lengthBetween(Bean::getString1, 11, 100));
    assertEquals(1, f.errors.size());
    ve = f.findErrorOfTypeForField(LengthMustBeBetween.class, Bean::getString1);
    assertNotNull(ve);
    assertEquals(11, ve.getMessageArgs()[0]);
    assertEquals(100, ve.getMessageArgs()[1]);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.lengthBetween(Bean::getString1, 5, 10));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.lengthBetween(Bean::getString1, 10, 15));
    assertEquals(0, f.errors.size());

    // valid - middle
    assertTrue(f.lengthBetween(Bean::getString1, 5, 150));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_LengthMustNotBeBetween() {
    Bean bean = new Bean();
    bean.setString1("0123456789");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(
        IllegalArgumentException.class, () -> f.lengthNotBetween(Bean::getString2, -1, 100));
    assertThrows(
        IllegalArgumentException.class, () -> f.lengthNotBetween(Bean::getString2, 100, -1));
    assertThrows(
        IllegalArgumentException.class, () -> f.lengthNotBetween(Bean::getString2, 100, 50));
    assertThrows(IllegalArgumentException.class, () -> f.lengthNotBetween(Bean::getString2, 50, 50));

    // null value
    assertTrue(f.lengthNotBetween(Bean::getString2, 1, 10));
    assertEquals(0, f.errors.size());

    // invalid - left edge case
    f.errors.clear();
    assertFalse(f.lengthNotBetween(Bean::getString1, 5, 10));
    assertEquals(1, f.errors.size());
    LengthMustNotBeBetween ve =
        f.findErrorOfTypeForField(LengthMustNotBeBetween.class, Bean::getString1);
    assertNotNull(ve);
    assertEquals(5, ve.getMessageArgs()[0]);
    assertEquals(10, ve.getMessageArgs()[1]);

    // invalid - right edge case
    f.errors.clear();
    assertFalse(f.lengthNotBetween(Bean::getString1, 10, 100));
    assertEquals(1, f.errors.size());
    ve = f.findErrorOfTypeForField(LengthMustNotBeBetween.class, Bean::getString1);
    assertNotNull(ve);
    assertEquals(10, ve.getMessageArgs()[0]);
    assertEquals(100, ve.getMessageArgs()[1]);

    f.errors.clear();
    // valid - left edge case
    assertTrue(f.lengthNotBetween(Bean::getString1, 5, 9));
    assertEquals(0, f.errors.size());

    // valid - right edge case
    assertTrue(f.lengthNotBetween(Bean::getString1, 11, 15));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_Contains() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.contains(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.contains(Bean::getString1, ""));

    // null value
    assertFalse(f.contains(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.contains(Bean::getString1, "ABC"));
    assertTrue(f.contains(r -> r.getString1().toUpperCase(), "FGH"));

    // invalid case
    f.errors.clear();
    assertFalse(f.contains(Bean::getString1, "123"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "123", f.findErrorOfTypeForField(MustContain.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_NotContains() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notContains(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.notContains(Bean::getString1, ""));

    // null value
    assertFalse(f.notContains(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.notContains(Bean::getString1, "123"));
    assertTrue(f.notContains(Bean::getString1, "FGH"));

    // invalid case
    f.errors.clear();
    assertFalse(f.notContains(Bean::getString1, "ABC"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "ABC",
        f.findErrorOfTypeForField(MustNotContain.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_StartsWith() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.startsWith(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.startsWith(Bean::getString1, ""));

    // null value
    assertFalse(f.startsWith(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.startsWith(Bean::getString1, "AB"));
    assertTrue(f.startsWith(r -> r.getString1().toLowerCase(), "ab"));

    // invalid case
    f.errors.clear();
    assertFalse(f.startsWith(Bean::getString1, "123"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "123", f.findErrorOfTypeForField(MustStartWith.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_NotStartsWith() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notStartsWith(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.notStartsWith(Bean::getString1, ""));

    // null value
    assertFalse(f.notStartsWith(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.notStartsWith(Bean::getString1, "123"));

    // invalid case
    f.errors.clear();
    assertFalse(f.notStartsWith(Bean::getString1, "ABC"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "ABC",
        f.findErrorOfTypeForField(MustNotStartWith.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_EndsWith() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.endsWith(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.endsWith(Bean::getString1, ""));

    // null value
    assertFalse(f.endsWith(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.endsWith(Bean::getString1, "fgh"));
    assertTrue(f.endsWith(r -> r.getString1().toUpperCase(), "GH"));

    // invalid case
    f.errors.clear();
    assertFalse(f.endsWith(Bean::getString1, "123"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "123", f.findErrorOfTypeForField(MustEndWith.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_NotEndsWith() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.notEndsWith(Bean::getString1, null));
    assertThrows(IllegalArgumentException.class, () -> f.notEndsWith(Bean::getString1, ""));

    // null value
    assertFalse(f.notEndsWith(Bean::getString2, "asd"));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString2));

    // valid cases
    assertTrue(f.notEndsWith(Bean::getString1, "123"));
    assertTrue(f.notEndsWith(r -> r.getString1().toUpperCase(), "gh"));

    // invalid case
    f.errors.clear();
    assertFalse(f.notEndsWith(Bean::getString1, "fgh"));
    assertEquals(1, f.errors.size());
    assertEquals(
        "fgh",
        f.findErrorOfTypeForField(MustNotEndWith.class, Bean::getString1).getMessageArgs()[0]);
  }

  @Test
  void test_HasText() {
    Bean bean = new Bean();
    bean.setString1("ABCDefgh");
    var f = validationContextFactory.buildFor(bean);

    // valid cases
    assertTrue(f.hasText(Bean::getString1));

    // invalid case - null
    bean.setString2(null);
    f.errors.clear();
    assertFalse(f.hasText(Bean::getString2));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustHaveText.class, Bean::getString2));

    // invalid case - empty
    bean.setString2("");
    f.errors.clear();
    assertFalse(f.hasText(Bean::getString2));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustHaveText.class, Bean::getString2));

    // invalid case - whitespaces
    bean.setString2("\u005Ct" + "\u005Cn" + "\u005Cf" + "\u005Cr");
    f.errors.clear();
    assertFalse(f.hasText(Bean::getString2));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustHaveText.class, Bean::getString2));
  }

  @Test
  void test_LengthLe() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.lengthLe(Bean::getString1, -1));

    // valid - null value
    bean.setString2(null);
    assertTrue(f.lengthLe(Bean::getString2, 0));

    // valid - good value
    bean.setString2("0123456789");
    assertTrue(f.lengthLe(Bean::getString2, 10)); // edge case
    assertTrue(f.lengthLe(Bean::getString2, 11));

    // invalid case
    assertFalse(f.lengthLe(Bean::getString2, 9));
    assertEquals(1, f.errors.size());
    assertEquals(
        9,
        f.findErrorOfTypeForField(LengthMustBeLessOrEqual.class, Bean::getString2)
            .getMessageArgs()[0]);
  }

  @Test
  void test_LengthLess() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.lengthLess(Bean::getString1, 0));

    // valid - null value
    bean.setString2(null);
    assertTrue(f.lengthLess(Bean::getString2, 1));

    // valid - good value
    bean.setString2("0123456789");
    assertTrue(f.lengthLess(Bean::getString2, 11));

    // invalid case
    assertFalse(f.lengthLess(Bean::getString2, 10));
    assertEquals(1, f.errors.size());
    assertEquals(
        10, f.findErrorOfTypeForField(LengthMustBeLess.class, Bean::getString2).getMessageArgs()[0]);
  }

  @Test
  void test_LengthGe() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.lengthGe(Bean::getString1, -1));

    // valid - null value
    bean.setString2(null);
    assertTrue(f.lengthGe(Bean::getString2, 0));

    // valid - good value
    bean.setString2("0123456789");
    assertTrue(f.lengthGe(Bean::getString2, 10)); // edge case
    assertTrue(f.lengthGe(Bean::getString2, 9));

    // invalid case
    assertFalse(f.lengthGe(Bean::getString2, 11));
    assertEquals(1, f.errors.size());
    assertEquals(
        11,
        f.findErrorOfTypeForField(LengthMustBeGreaterOrEqual.class, Bean::getString2)
            .getMessageArgs()[0]);
  }

  @Test
  void test_LengthGreater() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // illegal args
    assertThrows(IllegalArgumentException.class, () -> f.lengthGreater(Bean::getString1, -1));

    // valid - good value
    bean.setString2("0123456789");
    assertTrue(f.lengthGreater(Bean::getString2, 9));

    // invalid - null value
    f.errors.clear();
    bean.setString2(null);
    assertFalse(f.lengthGreater(Bean::getString2, 0));
    assertEquals(1, f.errors.size());
    assertEquals(
        0,
        f.findErrorOfTypeForField(LengthMustBeGreater.class, Bean::getString2).getMessageArgs()[0]);

    // invalid 2
    f.errors.clear();
    assertFalse(f.lengthGreater(Bean::getString2, 10));
    assertEquals(1, f.errors.size());
    assertEquals(
        10,
        f.findErrorOfTypeForField(LengthMustBeGreater.class, Bean::getString2).getMessageArgs()[0]);
  }

  @Test
  void test_NotEmpty() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // valid case
    bean.setList(Arrays.asList(123));
    assertTrue(f.notEmpty(Bean::getList));

    // invalid case - null
    f.errors.clear();
    bean.setList(null);
    assertFalse(f.notEmpty(Bean::getList));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeEmpty.class, Bean::getList));

    // invalid case - empty
    f.errors.clear();
    bean.setList(Collections.emptyList());
    assertFalse(f.notEmpty(Bean::getList));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeEmpty.class, Bean::getList));
  }

  @Test
  void test_Empty() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // valid case - null
    bean.setList(null);
    assertTrue(f.empty(Bean::getList));

    // valid case - empty
    bean.setList(new LinkedList<>());
    assertTrue(f.empty(Bean::getList));

    // invalid case - elements
    f.errors.clear();
    bean.setList(Arrays.asList(1));
    assertFalse(f.empty(Bean::getList));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustBeEmpty.class, Bean::getList));
  }

  @Test
  void test_Email() {
    Bean bean = new Bean();
    var f = validationContextFactory.buildFor(bean);

    // valid
    bean.setString1("asd@asd.com");
    assertTrue(f.validEmail(Bean::getString1));

    // invalid - null input
    f.errors.clear();
    bean.setString2("asd@.com");
    assertFalse(f.validEmail(Bean::getString2));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustBeValidEmail.class, Bean::getString2));

    // invalid - wrong input
    f.errors.clear();
    bean.setString2(null);
    assertFalse(f.validEmail(Bean::getString2));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustBeValidEmail.class, Bean::getString2));
  }

  private ObjectValidator<Bean> beanValidator =
      new ObjectValidator<Bean>() {
        @Override
        public void validate(
            Bean subject,
            String propertyName,
            ValidationContext<Bean> ctx,
            Collection<Bean> optionalSubjectCollection,
            ValidationContext<?> parentCtx) {

          ctx.hasText(Bean::getString1);
          ctx.hasText(Bean::getString2);
        }
      };

  @Test
  void test_ValidateAggregatedObject() {
    Beans beans = new Beans();
    var f = validationContextFactory.buildFor(beans);

    // Illegal
    assertThrows(IllegalArgumentException.class, () -> f.validateObject(Beans::getBean1, null));

    // valid case - field is null
    ValidationErrors vee = f.validateObject(Beans::getBean1, beanValidator);
    assertNull(vee);
    assertEquals(0, f.errors.size());

    // valid case - valid data
    Bean bean1 = new Bean();
    bean1.setString1("str1");
    bean1.setString2("str2");
    beans.setBean1(bean1);
    vee = f.validateObject(Beans::getBean1, beanValidator);
    assertNull(vee);
    assertEquals(0, f.errors.size());

    // invalid case
    bean1.setString1("");
    vee = f.validateObject(Beans::getBean1, beanValidator);
    assertNotNull(vee);
    assertNotNull(vee.findErrorOfTypeForField(MustHaveText.class, "string1"));
  }

  @Test
  void test_ValidateAggregatedObjects() {
    Beans beans = new Beans();
    var f = validationContextFactory.buildFor(beans);

    // Illegal - no validator
    assertThrows(IllegalArgumentException.class, () -> f.validateCollection(Beans::getBeans, null));

    // valid - list element null - no reaction
    beans.setBeans(Arrays.asList(null, null));
    f.validateCollection(Beans::getBeans, beanValidator);
    assertEquals(0, f.errors.size());

    // valid case - field is null
    beans.setBeans(null);
    ValidationErrors vee = f.validateCollection(Beans::getBeans, beanValidator);
    assertNull(vee);
    assertEquals(0, f.errors.size());

    // valid case - collection empty
    beans.setBeans(Collections.emptyList());
    vee = f.validateCollection(Beans::getBeans, beanValidator);
    assertNull(vee);
    assertEquals(0, f.errors.size());

    // valid case - valid data
    Bean bean1 = new Bean();
    bean1.setString1("str1");
    bean1.setString2("str2");
    beans.setBeans(Arrays.asList(bean1));
    vee = f.validateCollection(Beans::getBeans, beanValidator);
    assertNull(vee);
    assertEquals(0, f.errors.size());

    // invalid case
    bean1 = new Bean();
    bean1.setString1("str1");
    bean1.setString2("str2");

    Bean bean2 = new Bean();
    bean2.setString1("str1");
    bean2.setString2("");
    beans.setBeans(Arrays.asList(bean1, bean2));

    vee = f.validateCollection(Beans::getBeans, beanValidator);
    assertNotNull(vee);
    ValidationErrors ve = vee.findAggregatedErrorsAtIndex(1);
    assertNotNull(ve);
    assertNotNull(ve.findErrorOfTypeForField(MustHaveText.class, "string2"));

    assertNull(vee.findAggregatedErrorsAtIndex(0));

    assertTrue(vee.getList().get(0) == ve);
  }

  @Test
  void test_Matches() {
    Bean bean = new Bean();
    bean.setString2("2004");

    var f = validationContextFactory.buildFor(bean);

    // illegal - matcher == null
    assertThrows(
        IllegalArgumentException.class,
        () -> f.matches(Bean::getString1, null, MustMatchPattern.MESSAGE_CODE));

    // illegal - messageCode == null
    assertThrows(IllegalArgumentException.class, () -> f.matches(Bean::getString1, s -> true, null));

    // illegal - messageCode == ""
    assertThrows(IllegalArgumentException.class, () -> f.matches(Bean::getString1, s -> true, ""));

    // illegal - messageCode == " "
    assertThrows(IllegalArgumentException.class, () -> f.matches(Bean::getString1, s -> true, " "));

    // invalid - null
    assertFalse(f.matches(Bean::getString1, s -> true, MustMatchPattern.MESSAGE_CODE));
    assertEquals(1, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Bean::getString1));

    // invalid - doesn't match
    assertFalse(f.matches(Bean::getString2, s -> false, MustMatchPattern.MESSAGE_CODE));
    assertEquals(2, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustMatchPattern.class, Bean::getString2));

    // valid - match
    f.errors.clear();
    assertTrue(f.matches(Bean::getString2, s -> true, MustMatchPattern.MESSAGE_CODE));
    assertEquals(0, f.errors.size());
  }

  @Test
  void test_NowResolver() {
    var f = validationContextFactory.buildFor(new Dated());

    assertThrows(IllegalArgumentException.class, () -> f.setNowResolver(null));

    NowResolverImpl resolver = new NowResolverImpl();
    f.setNowResolver(resolver);
    assertEquals(resolver, f.getNowResolver());
  }

  @Test
  void test_Temporals_PastAll() {
    Clock pastClock = Clock.offset(Clock.systemUTC(), Duration.ofDays(-400));
    Dated r = buildNow(pastClock);

    var f = validationContextFactory.buildFor(r);

    // NOTE: In this test we tackle each temporal type just to make sure it will work

    assertTrue(f.past(Dated::getValueDate));
    assertTrue(f.past(Dated::getValueCalendar));
    assertTrue(f.past(Dated::getValueInstant));
    assertTrue(f.past(Dated::getValueLocalDate));
    assertTrue(f.past(Dated::getValueLocalDateTime));
    assertTrue(f.past(Dated::getValueLocalTime));
    assertTrue(f.past(Dated::getValueMonthDay));
    assertTrue(f.past(Dated::getValueOffsetDateTime));
    assertTrue(f.past(Dated::getValueOffsetTime));
    assertTrue(f.past(Dated::getValueYear));
    assertTrue(f.past(Dated::getValueYearMonth));
    assertTrue(f.past(Dated::getValueZonedDateTime));
    assertTrue(f.past(Dated::getValueHijrahDate));
    assertTrue(f.past(Dated::getValueJapaneseDate));
    assertTrue(f.past(Dated::getValueMinguoDate));
    assertTrue(f.past(Dated::getValueThaiBuddhistDate));

    assertTrue(f.pastOrPresent(Dated::getValueDate));
    assertTrue(f.pastOrPresent(Dated::getValueCalendar));
    assertTrue(f.pastOrPresent(Dated::getValueInstant));
    assertTrue(f.pastOrPresent(Dated::getValueLocalDate));
    assertTrue(f.pastOrPresent(Dated::getValueLocalDateTime));
    assertTrue(f.pastOrPresent(Dated::getValueLocalTime));
    assertTrue(f.pastOrPresent(Dated::getValueMonthDay));
    assertTrue(f.pastOrPresent(Dated::getValueOffsetDateTime));
    assertTrue(f.pastOrPresent(Dated::getValueOffsetTime));
    assertTrue(f.pastOrPresent(Dated::getValueYear));
    assertTrue(f.pastOrPresent(Dated::getValueYearMonth));
    assertTrue(f.pastOrPresent(Dated::getValueZonedDateTime));
    assertTrue(f.pastOrPresent(Dated::getValueHijrahDate));
    assertTrue(f.pastOrPresent(Dated::getValueJapaneseDate));
    assertTrue(f.pastOrPresent(Dated::getValueMinguoDate));
    assertTrue(f.pastOrPresent(Dated::getValueThaiBuddhistDate));

    assertFalse(f.future(Dated::getValueDate));
    assertFalse(f.future(Dated::getValueCalendar));
    assertFalse(f.future(Dated::getValueInstant));
    assertFalse(f.future(Dated::getValueLocalDate));
    assertFalse(f.future(Dated::getValueLocalDateTime));
    assertFalse(f.future(Dated::getValueLocalTime));
    assertFalse(f.future(Dated::getValueMonthDay));
    assertFalse(f.future(Dated::getValueOffsetDateTime));
    assertFalse(f.future(Dated::getValueOffsetTime));
    assertFalse(f.future(Dated::getValueYear));
    assertFalse(f.future(Dated::getValueYearMonth));
    assertFalse(f.future(Dated::getValueZonedDateTime));
    assertFalse(f.future(Dated::getValueHijrahDate));
    assertFalse(f.future(Dated::getValueJapaneseDate));
    assertFalse(f.future(Dated::getValueMinguoDate));
    assertFalse(f.future(Dated::getValueThaiBuddhistDate));

    assertFalse(f.futureOrPresent(Dated::getValueDate));
    assertFalse(f.futureOrPresent(Dated::getValueCalendar));
    assertFalse(f.futureOrPresent(Dated::getValueInstant));
    assertFalse(f.futureOrPresent(Dated::getValueLocalDate));
    assertFalse(f.futureOrPresent(Dated::getValueLocalDateTime));
    assertFalse(f.futureOrPresent(Dated::getValueLocalTime));
    assertFalse(f.futureOrPresent(Dated::getValueMonthDay));
    assertFalse(f.futureOrPresent(Dated::getValueOffsetDateTime));
    assertFalse(f.futureOrPresent(Dated::getValueOffsetTime));
    assertFalse(f.futureOrPresent(Dated::getValueYear));
    assertFalse(f.futureOrPresent(Dated::getValueYearMonth));
    assertFalse(f.futureOrPresent(Dated::getValueZonedDateTime));
    assertFalse(f.futureOrPresent(Dated::getValueHijrahDate));
    assertFalse(f.futureOrPresent(Dated::getValueJapaneseDate));
    assertFalse(f.futureOrPresent(Dated::getValueMinguoDate));
    assertFalse(f.futureOrPresent(Dated::getValueThaiBuddhistDate));
    assertNotNull(f.findErrorOfTypeForField(MustBeInFutureOrPresent.class, Dated::getValueInstant));
  }

  @Test
  void test_Temporals_Future() {
    Clock pastClock = Clock.offset(Clock.systemUTC(), Duration.ofDays(400));
    Dated r = buildNow(pastClock);

    var f = validationContextFactory.buildFor(r);

    assertFalse(f.past(Dated::getValueDate));
    assertFalse(f.pastOrPresent(Dated::getValueCalendar));
    assertTrue(f.future(Dated::getValueInstant));
    assertTrue(f.futureOrPresent(Dated::getValueLocalDate));

    assertEquals(2, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustBeInPast.class, Dated::getValueDate));
    assertNotNull(f.findErrorOfTypeForField(MustBeInPastOrPresent.class, Dated::getValueCalendar));
  }

  @Test
  void test_Temporals_Present() {
    Clock clock = Clock.fixed(Clock.systemUTC().instant(), ZoneId.systemDefault());
    Dated r = buildNow(clock);

    var f = validationContextFactory.buildFor(r);
    f.setNowResolver(new NowResolverImpl(clock));

    assertFalse(f.past(Dated::getValueDate));
    assertTrue(f.pastOrPresent(Dated::getValueCalendar));
    assertFalse(f.future(Dated::getValueInstant));
    assertTrue(f.futureOrPresent(Dated::getValueLocalDate));

    assertEquals(2, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustBeInPast.class, Dated::getValueDate));
    assertNotNull(f.findErrorOfTypeForField(MustBeInFuture.class, Dated::getValueInstant));
  }

  @Test
  void test_Temporals_Nulls() {
    var f = validationContextFactory.buildFor(new Dated());

    assertFalse(f.past(Dated::getValueDate));
    assertFalse(f.pastOrPresent(Dated::getValueCalendar));
    assertFalse(f.future(Dated::getValueInstant));
    assertFalse(f.futureOrPresent(Dated::getValueLocalDate));

    assertEquals(4, f.errors.size());
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Dated::getValueDate));
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Dated::getValueCalendar));
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Dated::getValueInstant));
    assertNotNull(f.findErrorOfTypeForField(MustNotBeNull.class, Dated::getValueLocalDate));
  }

  protected Dated buildNow(Clock clock) {
    Dated r = new Dated();
    r.setValueDate(Date.from(clock.instant()));
    r.setValueCalendar(GregorianCalendar.from(ZonedDateTime.now(clock)));
    r.setValueInstant(clock.instant());
    r.setValueLocalDate(LocalDate.now(clock));
    r.setValueLocalDateTime(LocalDateTime.now(clock));
    r.setValueLocalTime(LocalTime.now(clock));
    r.setValueMonthDay(MonthDay.now(clock));
    r.setValueOffsetDateTime(OffsetDateTime.now(clock));
    r.setValueOffsetTime(OffsetTime.now(clock));
    r.setValueYear(Year.now(clock));
    r.setValueYearMonth(YearMonth.now(clock));
    r.setValueZonedDateTime(ZonedDateTime.now(clock));
    r.setValueHijrahDate(HijrahDate.now(clock));
    r.setValueJapaneseDate(JapaneseDate.now(clock));
    r.setValueMinguoDate(MinguoDate.now(clock));
    r.setValueThaiBuddhistDate(ThaiBuddhistDate.now(clock));
    return r;
  }

  @Test
  void test_buildTemporalBoundaryMatchingTypeOfValidationSubject() {
    var f = validationContextFactory.buildFor(new Dated());

    assertThrows(
        IllegalArgumentException.class,
        () -> f.buildTemporalBoundaryMatchingTypeOfValidationSubject(null));

    assertThrows(
        IllegalArgumentException.class,
        () -> f.buildTemporalBoundaryMatchingTypeOfValidationSubject("str"));

    assertNotNull(f.buildTemporalBoundaryMatchingTypeOfValidationSubject(LocalDate.now()));
  }
}
