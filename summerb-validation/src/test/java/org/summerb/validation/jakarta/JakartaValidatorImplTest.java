package org.summerb.validation.jakarta;

import static org.junit.jupiter.api.Assertions.*;
import static org.summerb.validation.Asserts.assertIae;
import static org.summerb.validation.Asserts.assertIaeMessage;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactory;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;
import org.summerb.utils.clock.NowResolverImpl;
import org.summerb.validation.ThrowingRunnable;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationContextFactory;
import org.summerb.validation.ValidationContextFactoryImpl;
import org.summerb.validation.ValidationError;
import org.summerb.validation.errors.LengthMustBeBetween;
import org.summerb.validation.errors.LengthMustBeGreater;
import org.summerb.validation.errors.MustBeFalse;
import org.summerb.validation.errors.MustBeGreater;
import org.summerb.validation.errors.MustBeGreaterOrEqual;
import org.summerb.validation.errors.MustBeInFuture;
import org.summerb.validation.errors.MustBeInFutureOrPresent;
import org.summerb.validation.errors.MustBeInPast;
import org.summerb.validation.errors.MustBeInPastOrPresent;
import org.summerb.validation.errors.MustBeLess;
import org.summerb.validation.errors.MustBeLessOrEqual;
import org.summerb.validation.errors.MustBeNull;
import org.summerb.validation.errors.MustBeTrue;
import org.summerb.validation.errors.MustBeValidEmail;
import org.summerb.validation.errors.MustHaveText;
import org.summerb.validation.errors.MustMatchPattern;
import org.summerb.validation.errors.MustNotBeEmpty;
import org.summerb.validation.errors.MustNotBeNull;
import org.summerb.validation.jakarta.processors.AssertFalseProcessor;
import org.summerb.validation.jakarta.processors.AssertTrueProcessor;
import org.summerb.validation.jakarta.processors.DigitsProcessor;
import org.summerb.validation.jakarta.processors.EmailProcessor;
import org.summerb.validation.jakarta.processors.FutureOrPresentProcessor;
import org.summerb.validation.jakarta.processors.FutureProcessor;
import org.summerb.validation.jakarta.processors.NegativeProcessor;
import org.summerb.validation.jakarta.processors.NotBlankProcessor;
import org.summerb.validation.jakarta.processors.NotEmptyProcessor;
import org.summerb.validation.jakarta.processors.NotNullProcessor;
import org.summerb.validation.jakarta.processors.NullProcessor;
import org.summerb.validation.jakarta.processors.PastOrPresentProcessor;
import org.summerb.validation.jakarta.processors.PastProcessor;
import org.summerb.validation.jakarta.processors.PatternProcessor;
import org.summerb.validation.jakarta.processors.SizeProcessor;
import org.summerb.validation.testDtos.JakartaEdgeBean;
import org.summerb.validation.testDtos.JakartaFullBean;

public class JakartaValidatorImplTest {

  private static final Long L1 = 1L;

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  PropertyNameResolverFactory propertyNameResolverFactory =
      new PropertyNameResolverFactoryImpl(methodCapturerProxyClassFactory);

  JakartaAnnotationsProcessorsRegistry jakartaAnnotationsProcessorsRegistry =
      new JakartaAnnotationsProcessorsRegistryPackageScanImpl();

  JakartaValidationBeanProcessor jakartaValidationBeanProcessor =
      new JakartaValidationBeanProcessorCachedImpl(
          new JakartaValidationBeanProcessorImpl(jakartaAnnotationsProcessorsRegistry));

  ValidationContextFactory validationContextFactory =
      new ValidationContextFactoryImpl(
          propertyNameResolverFactory, new JakartaValidatorImpl(jakartaValidationBeanProcessor));

  protected <T extends Annotation> T annotation(
      Class<?> clazz, String field, Class<T> annotationClass) throws NoSuchFieldException {
    return clazz.getDeclaredField(field).getDeclaredAnnotation(annotationClass);
  }

  public static IllegalArgumentException assertCtxRequired(ThrowingRunnable runnable) {
    return assertIaeMessage(AnnotationProcessor.CTX_REQUIRED, runnable);
  }

  @Test
  void test_constructor_expectIae() {
    assertIae(() -> new JakartaValidatorImpl(null));
  }

  @Test
  void test_expectValid_becauseSubjectIsNull() {
    Clock now = Clock.fixed(Clock.systemUTC().instant(), Clock.systemUTC().getZone());
    JakartaFullBean p = buildValidPojo(now);

    JakartaValidatorImpl f = new JakartaValidatorImpl(jakartaValidationBeanProcessor);

    var ctx = validationContextFactory.buildFor(p);
    ctx.setNowResolver(new NowResolverImpl(now));

    f.findValidationErrors(null, ctx);

    assertEquals(0, ctx.getErrors().size());
  }

  @Test
  void test_expectValid_becauseOfValidData() {
    Clock now = Clock.fixed(Clock.systemUTC().instant(), Clock.systemUTC().getZone());
    JakartaFullBean p = buildValidPojo(now);

    var ctx = validationContextFactory.buildFor(p);
    ctx.setNowResolver(new NowResolverImpl(now));

    ctx.processJakartaValidations();
    assertEquals(0, ctx.getErrors().size());
  }

  protected JakartaFullBean buildValidPojo(Clock now) {
    Clock future = Clock.offset(now, Duration.ofDays(400).plusHours(1));
    Clock past = Clock.offset(now, Duration.ofDays(-400).minusHours(1));

    JakartaFullBean p = new JakartaFullBean();
    p.setSizeString("1234");
    p.setSizeStringBuilder(new StringBuilder("1234"));
    p.setSizeCollection(Arrays.asList("a", "a"));
    p.setSizeMap(Collections.singletonMap("a", "a"));
    p.setSizeArray(new String[] {"a"});

    p.setNotEmptyString("1234");
    p.setNotEmptyStringBuilder(new StringBuilder("1234"));
    p.setNotEmptyCollection(Arrays.asList("a", "a"));
    p.setNotEmptyMap(Collections.singletonMap("a", "a"));
    p.setNotEmptyArray(new String[] {"a"});

    p.setNotBlankString("1234");
    p.setNotBlankStringBuilder(new StringBuilder("1234"));

    p.setPositiveBigDecimal(BigDecimal.ONE);
    p.setPositiveBigInteger(BigInteger.ONE);
    p.setPositiveByte((byte) 1);
    p.setPositiveShort((short) 1);
    p.setPositiveInt(1);
    p.setPositiveLong((long) 1);
    p.setPositiveFloat(1f);
    p.setPositiveDouble(1d);

    p.setPositiveOrZeroLong(0);
    p.setNegativeLong(-1);
    p.setNegativeOrZeroLong(0);

    p.setMinBigDecimal(BigDecimal.valueOf(10));
    p.setMinBigInteger(BigInteger.valueOf(10));
    p.setMinByte((byte) 10);
    p.setMinShort((short) 10);
    p.setMinInt(10);
    p.setMinLong((long) 10);

    p.setMaxLong((long) 7);

    p.setDecimalMaxString("10.043");
    p.setDecimalMinBigDecimal(BigDecimal.valueOf(6));

    p.setDigitsBigDecimal(BigDecimal.valueOf(99998, 2));

    p.setPatternString("ASD");
    p.setPatternStringBuilder(new StringBuilder("123"));

    p.setEmailString("asd@asd.ru");
    p.setEmailStringBuilder(new StringBuilder("asd@asd.ru"));
    p.setEmailStringRegex("asd@asd.com");

    p.setNullString(null);
    p.setNotNullString("asd");
    p.setAssertTrueBoolean(true);
    p.setAssertFalseBoolean(false);

    p.setFutureDate(Date.from(future.instant()));
    p.setFutureCalendar(GregorianCalendar.from(ZonedDateTime.now(future)));
    p.setFutureInstant(future.instant());
    p.setFutureLocalDate(LocalDate.now(future));
    p.setFutureLocalDateTime(LocalDateTime.now(future));
    p.setFutureLocalTime(LocalTime.now(future));
    p.setFutureMonthDay(MonthDay.now(future));
    p.setFutureOffsetDateTime(OffsetDateTime.now(future));
    p.setFutureOffsetTime(OffsetTime.now(future));
    p.setFutureYear(Year.now(future));
    p.setFutureYearMonth(YearMonth.now(future));
    p.setFutureZonedDateTime(ZonedDateTime.now(future));
    p.setFutureHijrahDate(HijrahDate.now(future));
    p.setFutureJapaneseDate(JapaneseDate.now(future));
    p.setFutureMinguoDate(MinguoDate.now(future));
    p.setFutureThaiBuddhistDate(ThaiBuddhistDate.now(future));

    p.setFutureOrPresentYear(Year.now(now));
    p.setPastYear(Year.now(past));
    p.setPastOrPresentYear(Year.now(now));
    return p;
  }

  @Test
  void test_expectValid_becauseOfNullData() {
    JakartaFullBean p = new JakartaFullBean();

    // NOTE: Some jakarta annotations treat nulls as valid, but others -- not. So we have to provide
    // some values for non-tolerable annotations

    p.setNotEmptyString("1234");
    p.setNotEmptyStringBuilder(new StringBuilder("1234"));
    p.setNotEmptyCollection(Arrays.asList("a", "a"));
    p.setNotEmptyMap(Collections.singletonMap("a", "a"));
    p.setNotEmptyArray(new String[] {"a"});

    p.setNotBlankString("1234");
    p.setNotBlankStringBuilder(new StringBuilder("1234"));

    p.setPositiveByte((byte) 1);
    p.setPositiveShort((short) 1);
    p.setPositiveInt(1);
    p.setPositiveFloat(1f);

    p.setPositiveOrZeroLong(0);
    p.setNegativeLong(-1);
    p.setNegativeOrZeroLong(0);

    p.setMinByte((byte) 10);
    p.setMinShort((short) 10);

    p.setNotNullString("asd");

    var ctx = validationContextFactory.buildFor(p);
    ctx.processJakartaValidations();
    assertEquals(0, ctx.getErrors().size());
  }

  @Test
  void test_expectInvalid() {
    Clock now = Clock.fixed(Clock.systemUTC().instant(), Clock.systemUTC().getZone());

    Clock future = Clock.offset(now, Duration.ofDays(400).plusHours(1));
    Clock past = Clock.offset(now, Duration.ofDays(-400).minusHours(1));

    JakartaFullBean p = new JakartaFullBean();
    p.setSizeString("1");
    p.setSizeStringBuilder(new StringBuilder("1"));
    p.setSizeCollection(Collections.emptyList());
    p.setSizeMap(Collections.emptyMap());
    p.setSizeArray(new String[0]);

    p.setNotEmptyString("");
    p.setNotEmptyStringBuilder(new StringBuilder());
    p.setNotEmptyCollection(Collections.emptyList());
    p.setNotEmptyMap(Collections.emptyMap());
    p.setNotEmptyArray(new String[0]);

    p.setNotBlankString(" ");
    p.setNotBlankStringBuilder(new StringBuilder(" "));

    p.setPositiveBigDecimal(BigDecimal.ZERO);
    p.setPositiveBigInteger(BigInteger.ZERO);
    p.setPositiveByte((byte) 0);
    p.setPositiveShort((short) 0);
    p.setPositiveInt(0);
    p.setPositiveLong(0L);
    p.setPositiveFloat(-1f);
    p.setPositiveDouble(-1d);

    p.setPositiveOrZeroLong(-1);
    p.setNegativeLong(1);
    p.setNegativeOrZeroLong(1);

    p.setMinBigDecimal(BigDecimal.valueOf(3));
    p.setMinBigInteger(BigInteger.valueOf(3));
    p.setMinByte((byte) 3);
    p.setMinShort((short) 3);
    p.setMinInt(3);
    p.setMinLong((long) 3);

    p.setMaxLong((long) 15);

    p.setDecimalMaxString("10.06");
    p.setDecimalMinBigDecimal(BigDecimal.valueOf(3));

    p.setDigitsBigDecimal(BigDecimal.valueOf(199998, 2));

    p.setPatternString("123");
    p.setPatternStringBuilder(new StringBuilder("asd"));

    p.setEmailString("asd");
    p.setEmailStringBuilder(new StringBuilder("asd"));
    p.setEmailStringRegex("asd");
    p.setNullString("asd");
    p.setNotNullString(null);
    p.setAssertTrueBoolean(false);
    p.setAssertFalseBoolean(true);

    p.setFutureDate(Date.from(past.instant()));
    p.setFutureCalendar(GregorianCalendar.from(ZonedDateTime.now(past)));
    p.setFutureInstant(past.instant());
    p.setFutureLocalDate(LocalDate.now(past));
    p.setFutureLocalDateTime(LocalDateTime.now(past));
    p.setFutureLocalTime(LocalTime.now(past));
    p.setFutureMonthDay(MonthDay.now(past));
    p.setFutureOffsetDateTime(OffsetDateTime.now(past));
    p.setFutureOffsetTime(OffsetTime.now(past));
    p.setFutureYear(Year.now(past));
    p.setFutureYearMonth(YearMonth.now(past));
    p.setFutureZonedDateTime(ZonedDateTime.now(past));
    p.setFutureHijrahDate(HijrahDate.now(past));
    p.setFutureJapaneseDate(JapaneseDate.now(past));
    p.setFutureMinguoDate(MinguoDate.now(past));
    p.setFutureThaiBuddhistDate(ThaiBuddhistDate.now(past));

    p.setFutureOrPresentYear(Year.now(past));
    p.setPastYear(Year.now(now));
    p.setPastOrPresentYear(Year.now(future));

    var ctx = validationContextFactory.buildFor(p);
    ctx.setNowResolver(new NowResolverImpl(now));
    ctx.processJakartaValidations();

    // check each and every validation error existence
    assertVe(ctx, LengthMustBeBetween.class, JakartaFullBean::getSizeString, 3, 7);
    assertVe(ctx, LengthMustBeBetween.class, JakartaFullBean::getSizeStringBuilder, 3, 7);
    assertVe(ctx, LengthMustBeBetween.class, JakartaFullBean::getSizeCollection, 1, 3);
    assertVe(ctx, LengthMustBeBetween.class, JakartaFullBean::getSizeMap, 1, 3);
    assertVe(ctx, LengthMustBeBetween.class, JakartaFullBean::getSizeArray, 1, 3);

    assertVe(ctx, LengthMustBeGreater.class, JakartaFullBean::getNotEmptyString, 0);
    assertVe(ctx, LengthMustBeGreater.class, JakartaFullBean::getNotEmptyStringBuilder, 0);
    assertVe(ctx, MustNotBeEmpty.class, JakartaFullBean::getNotEmptyCollection);
    assertVe(ctx, MustNotBeEmpty.class, JakartaFullBean::getNotEmptyMap);
    assertVe(ctx, MustNotBeEmpty.class, JakartaFullBean::getNotEmptyArray);

    assertVe(ctx, MustHaveText.class, JakartaFullBean::getNotBlankString);
    assertVe(ctx, MustHaveText.class, JakartaFullBean::getNotBlankStringBuilder);

    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveBigDecimal, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveBigInteger, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveByte, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveShort, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveInt, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveLong, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveFloat, 0);
    assertVe(ctx, MustBeGreater.class, JakartaFullBean::getPositiveDouble, 0);

    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getPositiveOrZeroLong, 0);
    assertVe(ctx, MustBeLess.class, JakartaFullBean::getNegativeLong, 0);
    assertVe(ctx, MustBeLessOrEqual.class, JakartaFullBean::getNegativeOrZeroLong, 0);

    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinBigDecimal, 5L);
    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinBigInteger, 5L);
    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinByte, 5L);
    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinShort, 5L);
    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinInt, 5L);
    assertVe(ctx, MustBeGreaterOrEqual.class, JakartaFullBean::getMinLong, 5L);

    assertVe(ctx, MustBeLessOrEqual.class, JakartaFullBean::getMaxLong, 10L);

    assertVe(
        ctx,
        MustBeLessOrEqual.class,
        JakartaFullBean::getDecimalMaxString,
        BigDecimal.valueOf(10.05));
    assertVe(
        ctx,
        MustBeGreaterOrEqual.class,
        JakartaFullBean::getDecimalMinBigDecimal,
        BigDecimal.valueOf(5.05));

    assertVe(
        ctx,
        MustBeLessOrEqual.class,
        JakartaFullBean::getDigitsBigDecimal,
        BigDecimal.valueOf(999.99));
    assertVe(ctx, MustMatchPattern.class, JakartaFullBean::getPatternString);
    assertVe(ctx, MustMatchPattern.class, JakartaFullBean::getPatternStringBuilder);
    assertVe(ctx, MustBeValidEmail.class, JakartaFullBean::getEmailString);
    assertVe(ctx, MustBeValidEmail.class, JakartaFullBean::getEmailStringBuilder);
    assertVe(ctx, MustBeValidEmail.class, JakartaFullBean::getEmailStringRegex);
    assertVe(ctx, MustMatchPattern.class, JakartaFullBean::getEmailStringRegex);

    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureDate);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureCalendar);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureInstant);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureLocalDate);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureLocalDateTime);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureLocalTime);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureMonthDay);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureOffsetDateTime);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureOffsetTime);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureYear);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureYearMonth);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureZonedDateTime);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureHijrahDate);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureJapaneseDate);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureMinguoDate);
    assertVe(ctx, MustBeInFuture.class, JakartaFullBean::getFutureThaiBuddhistDate);

    assertVe(ctx, MustBeInFutureOrPresent.class, JakartaFullBean::getFutureOrPresentYear);
    assertVe(ctx, MustBeInPastOrPresent.class, JakartaFullBean::getPastOrPresentYear);
    assertVe(ctx, MustBeInPast.class, JakartaFullBean::getPastYear);

    assertVe(ctx, MustBeNull.class, JakartaFullBean::getNullString);
    assertVe(ctx, MustNotBeNull.class, JakartaFullBean::getNotNullString);

    assertVe(ctx, MustBeTrue.class, JakartaFullBean::getAssertTrueBoolean);
    assertVe(ctx, MustBeFalse.class, JakartaFullBean::isAssertFalseBoolean);

    // now just check count
    int expectedErrors = 62;
    if (ctx.getErrors().size() != expectedErrors) {
      System.out.println(new org.summerb.validation.ValidationErrors(ctx.getErrors()));
    }
    assertEquals(expectedErrors, ctx.getErrors().size());
  }

  protected <T extends ValidationError> void assertVe(
      ValidationContext<JakartaFullBean> ctx,
      Class<T> veClass,
      Function<JakartaFullBean, Object> getter,
      Object... args) {
    T ve = ctx.findErrorOfTypeForField(veClass, getter);
    assertNotNull(ve, veClass.getCanonicalName() + " not found");
    if (args != null && args.length == 0) {
      args = null;
    }
    if (args != null) {
      assertArrayEquals(args, ve.getMessageArgs());
    }
  }

  @Test
  void test_edgeCases() {
    JakartaEdgeBean p = new JakartaEdgeBean();
    p.setDecimalMaxString("10.5");
    p.setDecimalMinString("10.5");
    p.setNegativeOnNegaive(-5L);
    p.setNegativeOnZero(0L);
    p.setNegativeOnPositive(5L);
    p.setNotEmptyString(null /* null intentional */);
    p.setNotBlankString(null /* null intentional */);
    p.setSizeMinInclusiveBoundaryString("123");
    p.setSizeMaxInclusiveBoundaryString("12345");
    p.setSizeAboveMaxString("1234567890");
    p.setSizeBelowMinString("1");

    var ctx = validationContextFactory.buildFor(p);
    ctx.processJakartaValidations();

    assertNotNull(
        ctx.findErrorOfTypeForField(MustBeGreater.class, JakartaEdgeBean::getDecimalMinString));
    assertNotNull(
        ctx.findErrorOfTypeForField(MustBeLess.class, JakartaEdgeBean::getDecimalMaxString));

    assertNotNull(
        ctx.findErrorOfTypeForField(MustBeLess.class, JakartaEdgeBean::getNegativeOnPositive));
    assertNotNull(
        ctx.findErrorOfTypeForField(MustBeLess.class, JakartaEdgeBean::getNegativeOnZero));
    assertEquals(0, ctx.findErrorsForField(JakartaEdgeBean::getNegativeOnNegaive).size());

    assertNotNull(
        ctx.findErrorOfTypeForField(MustNotBeNull.class, JakartaEdgeBean::getNotEmptyString));

    assertNotNull(
        ctx.findErrorOfTypeForField(MustNotBeNull.class, JakartaEdgeBean::getNotBlankString));

    assertEquals(
        0, ctx.findErrorsForField(JakartaEdgeBean::getSizeMinInclusiveBoundaryString).size());
    assertEquals(
        0, ctx.findErrorsForField(JakartaEdgeBean::getSizeMaxInclusiveBoundaryString).size());
    assertNotNull(
        ctx.findErrorOfTypeForField(
            LengthMustBeBetween.class, JakartaEdgeBean::getSizeAboveMaxString));
    assertNotNull(
        ctx.findErrorOfTypeForField(
            LengthMustBeBetween.class, JakartaEdgeBean::getSizeBelowMinString));

    assertEquals(8, ctx.getErrors().size());
  }

  @Test
  void test_processors_expectIaeForAbstractImpl() {
    var c = JakartaFullBean.class;

    assertIaeMessage(
        "annotation required", () -> new EmailProcessor(null, "a").validate("a", null));

    assertIaeMessage(
        "propertyName required",
        () ->
            new EmailProcessor(annotation(c, "emailString", Email.class), null)
                .validate("a", null));
  }

  @Test
  void test_processors_expectIaeOnInvalidValueType() {
    var c = JakartaFullBean.class;
    var v = new ValidationContext<>();

    assertIae(
        () -> new EmailProcessor(annotation(c, "emailString", Email.class), "a").validate(L1, v));

    assertIae(
        () ->
            new AssertTrueProcessor(annotation(c, "assertTrueBoolean", AssertTrue.class), "a")
                .validate(L1, v));

    assertIae(
        () ->
            new AssertFalseProcessor(annotation(c, "assertFalseBoolean", AssertFalse.class), "a")
                .validate(L1, v));

    assertIae(
        () ->
            new FutureOrPresentProcessor(
                    annotation(c, "futureOrPresentYear", FutureOrPresent.class), "a")
                .validate(v, v));

    assertIae(
        () -> new FutureProcessor(annotation(c, "futureYear", Future.class), "a").validate(v, v));

    assertIae(() -> new PastProcessor(annotation(c, "pastYear", Past.class), "a").validate(v, v));

    assertIae(
        () ->
            new PastOrPresentProcessor(annotation(c, "pastOrPresentYear", PastOrPresent.class), "a")
                .validate(v, v));

    assertIae(
        () ->
            new NotBlankProcessor(annotation(c, "notBlankString", NotBlank.class), "a")
                .validate(L1, v));

    assertIae(
        () ->
            new PatternProcessor(annotation(c, "patternString", Pattern.class), "a")
                .validate(L1, v));

    assertIae(
        () ->
            new NotEmptyProcessor(annotation(c, "notEmptyString", NotEmpty.class), "a")
                .validate(L1, v));

    assertIae(
        () -> new SizeProcessor(annotation(c, "sizeString", Size.class), "a").validate(L1, v));

    assertIae(
        () ->
            new NegativeProcessor(annotation(c, "negativeLong", Negative.class), "a")
                .validate(v, v));

    assertIae(
        () ->
            new NegativeProcessor(annotation(c, "negativeLong", Negative.class), "a")
                .validate(new AtomicInteger(123), v));

    IllegalArgumentException ex =
        assertIae(
            () ->
                new DigitsProcessor(annotation(c, "digitsBigDecimal", Digits.class), "a")
                    .validate(v, v));
    assertTrue(ex.getMessage().startsWith("Type is not one of allowed"));
  }

  @Test
  void test_processors_expectIaeOnNullValidationContext() {
    var c = JakartaFullBean.class;

    assertCtxRequired(
        () ->
            new EmailProcessor(annotation(c, "emailString", Email.class), "a").validate("a", null));

    assertCtxRequired(
        () ->
            new AssertTrueProcessor(annotation(c, "assertTrueBoolean", AssertTrue.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new AssertFalseProcessor(annotation(c, "assertFalseBoolean", AssertFalse.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new FutureOrPresentProcessor(
                    annotation(c, "futureOrPresentYear", FutureOrPresent.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new FutureProcessor(annotation(c, "futureYear", Future.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () -> new PastProcessor(annotation(c, "pastYear", Past.class), "a").validate("a", null));

    assertCtxRequired(
        () ->
            new PastOrPresentProcessor(annotation(c, "pastOrPresentYear", PastOrPresent.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new NotBlankProcessor(annotation(c, "notBlankString", NotBlank.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new PatternProcessor(annotation(c, "patternString", Pattern.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () -> new NullProcessor(annotation(c, "nullString", Null.class), "a").validate("a", null));

    assertCtxRequired(
        () ->
            new NotNullProcessor(annotation(c, "notNullString", NotNull.class), "a")
                .validate("a", null));

    assertCtxRequired(
        () ->
            new NotEmptyProcessor(annotation(c, "notEmptyString", NotEmpty.class), "a")
                .validate("a", null));
  }
}
