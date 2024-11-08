package org.summerb.validation.errors;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.reflect.ClassPath;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.summerb.i18n.I18nUtils;
import org.summerb.validation.ValidationError;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ValidationMessageSourceContextConfig.class})
class MessageCodesIntegrTest {
  private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");

  @Autowired MessageSource messageSource;

  /**
   * In this test we want to ensure:
   *
   * <ul>
   *   <li>Each class in package <code></code> that implements {@link ValidationError} has field
   *       <code>public static final String MESSAGE_CODE</code>
   *   <li>Each message code has translation in EN and RU locales
   * </ul>
   *
   * This is mostly needed as a sanity-check in cases when we add new ValidationErrors but we do not
   * test them explicitly
   *
   * @throws Exception exception
   */
  @Test
  void test_expectAllClassesInErrorsPackageHaveMessageCodeAndTranslation() throws Exception {
    assertNotNull(messageSource);

    Set<Class<?>> validationErrorsClasses =
        ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
            .filter(
                clazz1 -> clazz1.getPackageName().equalsIgnoreCase("org.summerb.validation.errors"))
            .map(ClassPath.ClassInfo::load)
            .filter(ValidationError.class::isAssignableFrom)
            .collect(Collectors.toSet());

    assertFalse(
        validationErrorsClasses.isEmpty(),
        "package org.summerb.validation.errors must contain ValidationErrors. If you renamed pakcge -- make sure to update name in this test");

    for (Class<?> clazz : validationErrorsClasses) {
      String messageCode = getMessageCode(clazz);
      ValidationError ve = new ValidationError("pn", messageCode);
      assertHasTranslation(ve, messageCode);
    }
  }

  protected void assertHasTranslation(ValidationError error) throws Exception {
    String messageCode = getMessageCode(error.getClass());
    assertHasTranslation(error, messageCode);
  }

  protected void assertHasTranslation(ValidationError error, String messageCode) {
    String resultEn = I18nUtils.buildMessage(error, messageSource, Locale.ENGLISH);
    assertNotEquals(resultEn, messageCode);

    String resultRu = I18nUtils.buildMessage(error, messageSource, LOCALE_RU);
    assertNotEquals(resultRu, messageCode);
    assertNotEquals(resultRu, resultEn); // this will fail if only EN translation is provided
  }

  protected String getMessageCode(Class<?> clazz) throws IllegalAccessException {
    Field messageCodeField;
    try {
      messageCodeField = clazz.getDeclaredField("MESSAGE_CODE");
    } catch (NoSuchFieldException e) {
      fail("class " + clazz + " does not have \"public static final String MESSAGE_CODE\"");
      throw new RuntimeException("not reachable code");
    }
    assertTrue(Modifier.isStatic(messageCodeField.getModifiers()));
    Object value = messageCodeField.get(null);
    assertNotNull(value);
    assertEquals(String.class, value.getClass());
    return (String) value;
  }

  /**
   * This test is a redundant and thus more of a sanity check - we're explicitly testing particular
   * validation errors. All these are supposed to be already tested in {@link
   * #test_expectAllClassesInErrorsPackageHaveMessageCodeAndTranslation()} test, but again, just as
   * a sanity check -- we're testing them explicitly
   *
   * @throws Exception exception
   */
  @Test
  void test_expectAllMessageCodesToBeTranslated() throws Exception {
    assertNotNull(messageSource);

    assertHasTranslation(new DuplicateRecord("pn"));
    assertHasTranslation(new LengthMustBeBetween("pn", 1, 2));
    assertHasTranslation(new LengthMustBeGreater("pn", 1));
    assertHasTranslation(new LengthMustBeGreaterOrEqual("pn", 1));
    assertHasTranslation(new LengthMustBeLess("pn", 1));
    assertHasTranslation(new LengthMustBeLessOrEqual("pn", 1));
    assertHasTranslation(new LengthMustNotBeBetween("pn", 1, 2));
    assertHasTranslation(new MustBeBetween("pn", 1, 2));
    assertHasTranslation(new MustBeEmpty("pn"));
    assertHasTranslation(
        new MustBeEquals("pn", MustHaveText.MESSAGE_CODE, MustBeTrue.MESSAGE_CODE));
    assertHasTranslation(new MustBeEqualTo("pn", 1));
    assertHasTranslation(new MustBeFalse("pn"));
    assertHasTranslation(new MustBeGreater("pn", 1));
    assertHasTranslation(new MustBeGreaterOrEqual("pn", 1));
    assertHasTranslation(new MustBeIn("pn", Arrays.asList("1", "2")));
    assertHasTranslation(new MustBeLess("pn", 1));
    assertHasTranslation(new MustBeLessOrEqual("pn", 1));
    assertHasTranslation(new MustBeNull("pn"));
    assertHasTranslation(new MustBeTrue("pn"));
    assertHasTranslation(new MustBeValidEmail("pn"));
    assertHasTranslation(new MustContain("pn", "asd"));
    assertHasTranslation(new MustEndWith("pn", "asd"));
    assertHasTranslation(new MustHaveText("pn"));
    assertHasTranslation(new MustNotBeBetween("pn", 1, 2));
    assertHasTranslation(new MustNotBeEmpty("pn"));
    assertHasTranslation(new MustNotBeEqualTo("pn", 1));
    assertHasTranslation(new MustNotBeIn("pn", Arrays.asList("1", "2")));
    assertHasTranslation(new MustNotBeNull("pn"));
    assertHasTranslation(new MustNotContain("pn", "asd"));
    assertHasTranslation(new MustNotEndWith("pn", "asd"));
    assertHasTranslation(new MustNotHaveDuplicateName("pn"));
    assertHasTranslation(new MustNotStartWith("pn", "asd"));
    assertHasTranslation(new MustStartWith("pn", "asd"));
  }
}
