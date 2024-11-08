/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.validation;

import java.time.Instant;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.utils.clock.NowResolver;
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Validation context provides you with convenience methods to validate Bean instance (DTOs, DOMs,
 * Rows, etc..). Major killer-feature is ability to use "method references" which eliminate the need
 * to use string literals to denote property names. But this is not the only way -- each method has
 * it's old-school counterpart where you provide field name as string literal, AND/OR even combine
 * with Jakarta validation annotations processing (i.e. {@link NotBlank})
 *
 * <p>Intended use case example:
 *
 * <ol>
 *   <li>Create {@link ValidationContext} via calling {@link
 *       ValidationContextFactory#buildFor(Object)}
 *   <li>(optional, if combining with Jakarta annotations) call {@link #processJakartaValidations()}
 *       - all jakarta annotations on bean properties will be processed
 *   <li>Call validation methods, i.e. {@link #hasText(Function)}, etc...
 *   <li>Call {@link #throwIfHasErrors()} to throw {@link ValidationException} in case there were
 *       any errors
 * </ol>
 *
 * <p>For custom/complex validation rules you can do validation in your code and then just add
 * instances of (or sub-classes) {@link ValidationError}
 *
 * <p>In case you're validating Objects tree (hence the necessity to validate aggregated object(s))
 * there are convenient methods {@link #validateObject(Function, ObjectValidator)} and {@link
 * #validateCollection(Function, ObjectValidator)}
 *
 * <p>NOTE: validation methods which check if value adheres to some condition automatically assume
 * field is not null so it can actually be compared to boundary value/conditions. In case you want
 * to validate field only when it is not null (which is supposedly rare case), then please call
 * validation method only after ensuring value is not null, otherwise you'll get {@link
 * MustNotBeNull} validation error
 *
 * <p>Jakarta validations note: while direct usage of ValidationContext gives you full control over
 * validation logic, in some cases you don't need such level of control and therefore you can use
 * simpler declarative approach like annotating properties with constraints defined in <code>
 * jakarta.validation.constraints</code> package.
 *
 * <p>Performance note: although different approaches for implementing validations differ from
 * performance perspective, all of them are ok. Use validation approach which feels best for your
 * use. Here are results of 300000 validations blocks (1 block contains several validations):
 *
 * <pre>
 * ---------------------------------------------
 * ns         %     Task name
 * ---------------------------------------------
 * 4460890545  035%  Test: Jakarta annotations
 * 4220583657  033%  Test: ValidationContext w/ method references
 * 4161635622  032%  Test: ValidationContext w/ propertyNames
 *
 * Time per test (300000 iterations, of total 12843 ms):
 * Jakarta: 14869 (nanos) =	 0 (ms)
 * Getters: 14068 (nanos) =	 0 (ms)
 *   Names: 13872 (nanos) =	 0 (ms)
 * </pre>
 *
 * @author Sergey Karpushin
 * @param <T> type of Bean that is being validated. It is required if you will be using methods
 *     which accept "method references" (Functions, which are actually just lambdas)
 */
public class ValidationContext<T> {
  public static final Predicate<String> EMAIL_REGEXP =
      Pattern.compile(
              "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
              Pattern.CASE_INSENSITIVE)
          .asMatchPredicate();

  public static final Map<Class<?>, Function<NowResolver, Comparable<?>>> ALLOWED_TEMPORAL_TYPES;

  protected final T bean;
  protected PropertyNameResolver<T> propertyNameResolver;
  protected JakartaValidator jakartaValidator;
  protected ValidationContextFactory validationContextFactory;

  protected NowResolver nowResolver = new NowResolverImpl();

  protected final List<ValidationError> errors = new ArrayList<>();

  /**
   * Constructor for case when you'll use only methods where you'll specify property names and
   * values explicitly. You'll not be able to use:
   *
   * <ul>
   *   <li>methods which accept references to getters methods
   *   <li>jakarta annotations processing via {@link #processJakartaValidations()}
   * </ul>
   */
  public ValidationContext() {
    bean = null;
  }

  /**
   * Instantiates ValidationContext that is capable of translating methods references into property
   * names and also getting values from actual Bean -- this will reduce amount of code you're
   * writing and also you'll avoid usage of string literals for field names.
   *
   * <p>This constructor is not intended to be called directly -- use {@link
   * ValidationContextFactoryImpl#buildFor(Object)} to build such instance
   *
   * @param bean instance of a bean that is being validated in this context
   * @param propertyNameResolver impl of {@link PropertyNameResolver} for translating method
   *     references to property names
   * @param jakartaValidator optional -- validator that is capable of processing jakarta validations
   *     annotations. I.e. {@link NotEmpty} and others in same package
   * @param validationContextFactory optional -- needed only if you're going to validate aggregated
   *     objects via {@link #validateCollection(Function, ObjectValidator)} or {@link
   *     #validateObject(Function, ObjectValidator)}
   */
  public ValidationContext(
      T bean,
      PropertyNameResolver<T> propertyNameResolver,
      JakartaValidator jakartaValidator,
      ValidationContextFactory validationContextFactory) {
    Preconditions.checkArgument(bean != null, "bean required");
    Preconditions.checkArgument(propertyNameResolver != null, "propertyNameResolver required");

    this.bean = bean;
    this.propertyNameResolver = propertyNameResolver;
    this.jakartaValidator = jakartaValidator;
    this.validationContextFactory = validationContextFactory;
  }

  /**
   * This is supposedly will be a rarely used constructor. It will not allow to use method
   * references to denote field names, but it will be properly invoke objects and collections
   * validations where such (using method references) validation option is available;
   *
   * @param jakartaValidator optional -- validator that is capable of processing jakarta validations
   *     annotations. I.e. {@link NotEmpty} and others in same package
   * @param validationContextFactory optional -- needed only if you're going to validate aggregated
   *     objects via {@link #validateCollection(Function, ObjectValidator)} or {@link
   *     #validateObject(Function, ObjectValidator)}
   */
  public ValidationContext(
      JakartaValidator jakartaValidator, ValidationContextFactory validationContextFactory) {
    this.bean = null;
    this.jakartaValidator = jakartaValidator;
    this.validationContextFactory = validationContextFactory;
  }

  static {
    Map<Class<?>, Function<NowResolver, Comparable<?>>> allowed = new HashMap<>();
    allowed.put(Date.class, nr -> Date.from(nr.clock().instant()));
    allowed.put(Calendar.class, nr -> GregorianCalendar.from(ZonedDateTime.now(nr.clock())));
    allowed.put(Instant.class, nr -> nr.clock().instant());
    allowed.put(LocalDate.class, nr -> LocalDate.now(nr.clock()));
    allowed.put(LocalDateTime.class, nr -> LocalDateTime.now(nr.clock()));
    allowed.put(LocalTime.class, nr -> LocalTime.now(nr.clock()));
    allowed.put(MonthDay.class, nr -> MonthDay.now(nr.clock()));
    allowed.put(OffsetDateTime.class, nr -> OffsetDateTime.now(nr.clock()));
    allowed.put(OffsetTime.class, nr -> OffsetTime.now(nr.clock()));
    allowed.put(Year.class, nr -> Year.now(nr.clock()));
    allowed.put(YearMonth.class, nr -> YearMonth.now(nr.clock()));
    allowed.put(ZonedDateTime.class, nr -> ZonedDateTime.now(nr.clock()));
    allowed.put(HijrahDate.class, nr -> HijrahDate.now(nr.clock()));
    allowed.put(JapaneseDate.class, nr -> JapaneseDate.now(nr.clock()));
    allowed.put(MinguoDate.class, nr -> MinguoDate.now(nr.clock()));
    allowed.put(ThaiBuddhistDate.class, nr -> ThaiBuddhistDate.now(nr.clock()));
    ALLOWED_TEMPORAL_TYPES = Collections.unmodifiableMap(allowed);
  }

  public NowResolver getNowResolver() {
    return nowResolver;
  }

  /** @param nowResolver custom time resolver, usually needed for testing purposes */
  @VisibleForTesting
  public void setNowResolver(NowResolver nowResolver) {
    Preconditions.checkArgument(nowResolver != null);
    this.nowResolver = nowResolver;
  }

  /**
   * A handy method for running all known/discovered Jakarta validations on our bean. All errors
   * will be added to this context
   */
  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH",
      justification = "checked by preceeding Preconditions.checkState")
  public void processJakartaValidations() {
    Preconditions.checkState(
        jakartaValidator != null,
        "jakartaValidator must've been set in constructor before calling this method");

    jakartaValidator.findValidationErrors(bean, this);
  }

  public List<ValidationError> getErrors() {
    return errors;
  }

  public boolean isHasErrors() {
    return !errors.isEmpty();
  }

  /**
   * @param clazz type of validation error
   * @return true if error of such type present
   */
  public boolean hasErrorOfType(Class<? extends ValidationError> clazz) {
    return ValidationErrorsUtils.hasErrorOfType(clazz, errors);
  }

  /**
   * @param <E> type of error
   * @param clazz type of error
   * @return first encountered instance of such error, or null if not found
   */
  public <E extends ValidationError> E findErrorOfType(Class<E> clazz) {
    return ValidationErrorsUtils.findErrorOfType(clazz, errors);
  }

  /**
   * @param getter method reference used to obtain property name for which to find errors
   * @return list of errors for specified field, maybe empty, never null
   */
  public List<ValidationError> findErrorsForField(Function<T, Object> getter) {
    return ValidationErrorsUtils.findErrorsForField(getPropertyName(getter), errors);
  }

  /**
   * @param propertyName property name
   * @return list of errors for specified field, maybe empty, never null
   */
  public List<ValidationError> findErrorsForField(String propertyName) {
    return ValidationErrorsUtils.findErrorsForField(propertyName, errors);
  }

  /**
   * @param <E> error type
   * @param clazz error type
   * @param getter method reference used to obtain property name for which to find such error
   * @return instance of found error, or null if not found
   */
  public <E> E findErrorOfTypeForField(Class<E> clazz, Function<T, Object> getter) {
    return ValidationErrorsUtils.findErrorOfTypeForField(clazz, getPropertyName(getter), errors);
  }

  /**
   * @param <E> error type
   * @param clazz error type
   * @param propertyName property name for which to find such error
   * @return instance of found error, or null if not found
   */
  public <E> E findErrorOfTypeForField(Class<E> clazz, String propertyName) {
    return ValidationErrorsUtils.findErrorOfTypeForField(clazz, propertyName, errors);
  }

  public void add(ValidationError error) {
    Preconditions.checkArgument(error != null);
    Preconditions.checkArgument(
        StringUtils.hasText(error.getMessageCode()), "messageCode required");
    Preconditions.checkArgument(
        StringUtils.hasText(error.getPropertyName()), "propertyName required");

    if (isAlreadyHasSameErrorForSameField(error)) {
      return;
    }
    errors.add(error);
  }

  public boolean isAlreadyHasSameErrorForSameField(ValidationError error) {
    Preconditions.checkArgument(error != null);
    return errors.stream()
        .anyMatch(
            x ->
                x.getMessageCode().equals(error.getMessageCode())
                    && x.getPropertyName().equals(error.getPropertyName()));
  }

  public void throwIfHasErrors() {
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public String getPropertyName(Function<T, ?> getter) {
    Preconditions.checkState(propertyNameResolver != null, "propertyNameResolver is not provided");
    Preconditions.checkArgument(getter != null, "getPropertyName: getter required");
    return propertyNameResolver.resolve(getter);
  }

  protected <V> V getValue(Function<T, V> getter) {
    Preconditions.checkState(bean != null, "bean is required for this method to work");
    Preconditions.checkArgument(getter != null, "getter required");
    return getter.apply(bean);
  }

  /**
   * Method for invoking validation on an aggregated object.
   *
   * @param <V> type of aggregated object
   * @param aggregatedObjectGetter a getter that can be used to retrieve aggregated object instance
   * @param objectValidator validator logic
   * @return ValidationErrors if errors found, or null if no errors
   */
  public <V> ValidationErrors validateObject(
      Function<T, V> aggregatedObjectGetter, ObjectValidator<V> objectValidator) {
    return validateObject(
        getValue(aggregatedObjectGetter), objectValidator, getPropertyName(aggregatedObjectGetter));
  }

  public <V> ValidationErrors validateObject(
      V validationSubject, ObjectValidator<V> objectValidator, String propertyName) {
    Preconditions.checkArgument(objectValidator != null, "objectValidator required");
    if (validationSubject == null) {
      return null;
    }

    ValidationContext<V> ctx =
        validationContextFactory != null
            ? validationContextFactory.buildFor(validationSubject)
            : new ValidationContext<>();
    ValidationErrors validationErrors = new ValidationErrors(propertyName, ctx.getErrors());

    objectValidator.validate(validationSubject, propertyName, ctx, null, this);
    if (ctx.isHasErrors()) {
      errors.add(validationErrors);
      return validationErrors;
    }

    return null;
  }

  /**
   * Invoke validation on aggregated collection
   *
   * @param <V> type of items in collection
   * @param aggregatedObjectsGetter function for getting collection from bean
   * @param objectValidator validator that will be used to validate each item in this collection
   * @return {@link ValidationErrors} or null if no list
   */
  public <V> ValidationErrors validateCollection(
      Function<T, List<V>> aggregatedObjectsGetter, ObjectValidator<V> objectValidator) {
    return validateCollection(
        getValue(aggregatedObjectsGetter),
        objectValidator,
        getPropertyName(aggregatedObjectsGetter));
  }

  public <V> ValidationErrors validateCollection(
      List<V> validationSubjects, ObjectValidator<V> objectValidator, String propertyName) {
    Preconditions.checkArgument(objectValidator != null, "objectValidator required");
    if (validationSubjects == null) {
      return null;
    }

    ValidationErrors ret = new ValidationErrors(propertyName, new LinkedList<>());
    for (int i = 0; i < validationSubjects.size(); i++) {
      V validationSubject = validationSubjects.get(i);
      if (validationSubject == null) {
        continue;
      }

      ValidationContext<V> ctx =
          validationContextFactory != null
              ? validationContextFactory.buildFor(validationSubject)
              : new ValidationContext<>();
      objectValidator.validate(
          validationSubject, propertyName + "[" + i + "]", ctx, validationSubjects, this);
      if (ctx.isHasErrors()) {
        ret.add(new ValidationErrors(Integer.toString(i), ctx.getErrors()));
      }
    }

    if (ret.isHasErrors()) {
      errors.add(ret);
      return ret;
    }
    return null;
  }

  public boolean isNull(Function<T, Object> getter) {
    return isNull(getValue(getter), getPropertyName(getter));
  }

  public boolean isNull(Object value, String propertyName) {
    if (value == null) {
      return true;
    }

    add(new MustBeNull(propertyName));
    return false;
  }

  public boolean notNull(Function<T, Object> getter) {
    return notNull(getValue(getter), getPropertyName(getter));
  }

  public boolean notNull(Object value, String propertyName) {
    if (value != null) {
      return true;
    }

    add(new MustNotBeNull(propertyName));
    return false;
  }

  public boolean isTrue(Function<T, Boolean> getter) {
    return isTrue(getValue(getter), getPropertyName(getter));
  }

  public boolean isTrue(Boolean value, String propertyName) {
    if (Boolean.TRUE.equals(value)) {
      return true;
    }

    add(new MustBeTrue(propertyName));
    return false;
  }

  public boolean isFalse(Function<T, Boolean> getter) {
    return isFalse(getValue(getter), getPropertyName(getter));
  }

  public boolean isFalse(Boolean value, String propertyName) {
    if (Boolean.FALSE.equals(value)) {
      return true;
    }

    add(new MustBeFalse(propertyName));
    return false;
  }

  public <V> boolean eq(Function<T, V> getter, V value) {
    return eq(getValue(getter), value, getPropertyName(getter));
  }

  public <V> boolean eq(V value, V expectedValue, String propertyName) {
    if (ObjectUtils.nullSafeEquals(expectedValue, value)) {
      return true;
    }

    add(new MustBeEqualTo(propertyName, expectedValue));
    return false;
  }

  public <V> boolean ne(Function<T, V> getter, V value) {
    return ne(getValue(getter), value, getPropertyName(getter));
  }

  public <V> boolean ne(V value, V expectedValue, String propertyName) {
    if (!ObjectUtils.nullSafeEquals(expectedValue, value)) {
      return true;
    }

    add(new MustNotBeEqualTo(propertyName, value));
    return false;
  }

  public <V extends Comparable<V>> boolean less(Function<T, V> getter, V boundary) {
    return less(getValue(getter), boundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean less(V value, V boundary, String propertyName) {
    Preconditions.checkArgument(boundary != null, "boundary required");
    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.compareTo(boundary) < 0) {
      return true;
    }

    add(new MustBeLess(propertyName, boundary));
    return false;
  }

  public <V extends Comparable<V>> boolean le(Function<T, V> getter, V boundary) {
    return le(getValue(getter), boundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean le(V value, V boundary, String propertyName) {
    Preconditions.checkArgument(boundary != null, "boundary required");
    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.compareTo(boundary) <= 0) {
      return true;
    }

    add(new MustBeLessOrEqual(propertyName, boundary));
    return false;
  }

  public <V extends Comparable<V>> boolean greater(Function<T, V> getter, V boundary) {
    return greater(getValue(getter), boundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean greater(V value, V boundary, String propertyName) {
    Preconditions.checkArgument(boundary != null, "boundary required");
    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.compareTo(boundary) > 0) {
      return true;
    }

    add(new MustBeGreater(propertyName, boundary));
    return false;
  }

  public <V extends Comparable<V>> boolean ge(Function<T, V> getter, V boundary) {
    return ge(getValue(getter), boundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean ge(V value, V boundary, String propertyName) {
    Preconditions.checkArgument(boundary != null, "boundary required");
    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.compareTo(boundary) >= 0) {
      return true;
    }

    add(new MustBeGreaterOrEqual(propertyName, boundary));
    return false;
  }

  public <V> boolean in(Function<T, V> getter, Collection<V> values) {
    return in(getValue(getter), values, getPropertyName(getter));
  }

  public <V> boolean in(V value, Collection<V> values, String propertyName) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(values), "values collection must not be empty");

    if (values.contains(value)) {
      return true;
    }

    add(new MustBeIn(propertyName, values));
    return false;
  }

  public <V> boolean notIn(Function<T, V> getter, Collection<V> values) {
    return notIn(getValue(getter), values, getPropertyName(getter));
  }

  public <V> boolean notIn(V value, Collection<V> values, String propertyName) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(values), "values collection must not be empty");

    if (!values.contains(value)) {
      return true;
    }

    add(new MustNotBeIn(propertyName, values));
    return false;
  }

  public <V extends Comparable<V>> boolean between(
      Function<T, V> getter, V lowerBoundary, V upperBoundary) {
    return between(getValue(getter), lowerBoundary, upperBoundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean between(
      V value, V lowerBoundary, V upperBoundary, String propertyName) {
    assertRangeBoundaryParams(lowerBoundary, upperBoundary);

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (lowerBoundary.compareTo(value) <= 0 && value.compareTo(upperBoundary) <= 0) {
      return true;
    }

    add(new MustBeBetween(propertyName, lowerBoundary, upperBoundary));
    return false;
  }

  protected <V extends Comparable<V>> void assertRangeBoundaryParams(
      V lowerBoundary, V upperBoundary) {
    Preconditions.checkArgument(lowerBoundary != null, "lowerBoundary required");
    Preconditions.checkArgument(upperBoundary != null, "upperBoundary required");
    Preconditions.checkArgument(
        lowerBoundary.compareTo(upperBoundary) < 0,
        "lowerBoundary must be smaller than upperBoundary");
  }

  public <V extends Comparable<V>> boolean between(Function<T, V> getter, Range<V> range) {
    return between(getValue(getter), range, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean between(V value, Range<V> range, String propertyName) {
    assertRangeParam(range);

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (range.contains(value)) {
      return true;
    }

    add(new MustBeBetween(propertyName, range));
    return false;
  }

  protected <V extends Comparable<V>> void assertRangeParam(Range<V> range) {
    Preconditions.checkArgument(range != null, "range required");
    Preconditions.checkArgument(
        range.hasLowerBound() && range.hasUpperBound(), "range must be bounded");
  }

  public <V extends Comparable<V>> boolean notBetween(
      Function<T, V> getter, V lowerBoundary, V upperBoundary) {
    return notBetween(getValue(getter), lowerBoundary, upperBoundary, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean notBetween(
      V value, V lowerBoundary, V upperBoundary, String propertyName) {
    assertRangeBoundaryParams(lowerBoundary, upperBoundary);

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.compareTo(lowerBoundary) < 0 || upperBoundary.compareTo(value) < 0) {
      return true;
    }

    add(new MustNotBeBetween(propertyName, lowerBoundary, upperBoundary));
    return false;
  }

  public <V extends Comparable<V>> boolean notBetween(Function<T, V> getter, Range<V> range) {
    return notBetween(getValue(getter), range, getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean notBetween(
      V value, Range<V> range, String propertyName) {
    assertRangeParam(range);

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (!range.contains(value)) {
      return true;
    }

    add(new MustNotBeBetween(propertyName, range));
    return false;
  }

  public boolean lengthBetween(Function<T, String> getter, int lowerBoundary, int upperBoundary) {
    return lengthBetween(getValue(getter), lowerBoundary, upperBoundary, getPropertyName(getter));
  }

  public boolean lengthBetween(
      String value, int lowerBoundary, int upperBoundary, String propertyName) {
    assertLengthBetweenBoundaries(lowerBoundary, upperBoundary);

    int length = value == null ? 0 : value.length();
    if (lowerBoundary <= length && length <= upperBoundary) {
      return true;
    }

    add(new LengthMustBeBetween(propertyName, lowerBoundary, upperBoundary));
    return false;
  }

  protected void assertLengthBetweenBoundaries(int lowerBoundary, int upperBoundary) {
    Preconditions.checkArgument(lowerBoundary >= 0, "lowerBoundary must be non-negative");
    Preconditions.checkArgument(
        lowerBoundary < upperBoundary, "upperBoundary must be greater than lowerBoundary");
  }

  public boolean lengthNotBetween(
      Function<T, String> getter, int lowerBoundary, int upperBoundary) {
    return lengthNotBetween(
        getValue(getter), lowerBoundary, upperBoundary, getPropertyName(getter));
  }

  public boolean lengthNotBetween(
      String value, int lowerBoundary, int upperBoundary, String propertyName) {
    assertLengthBetweenBoundaries(lowerBoundary, upperBoundary);

    int length = value == null ? 0 : value.length();
    if (length < lowerBoundary || upperBoundary < length) {
      return true;
    }

    add(new LengthMustNotBeBetween(propertyName, lowerBoundary, upperBoundary));
    return false;
  }

  public boolean contains(Function<T, String> getter, String subString) {
    return contains(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean contains(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.contains(subString)) {
      return true;
    }

    add(new MustContain(propertyName, subString));
    return false;
  }

  public boolean notContains(Function<T, String> getter, String subString) {
    return notContains(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean notContains(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (!value.contains(subString)) {
      return true;
    }

    add(new MustNotContain(propertyName, subString));
    return false;
  }

  public boolean startsWith(Function<T, String> getter, String subString) {
    return startsWith(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean startsWith(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.startsWith(subString)) {
      return true;
    }

    add(new MustStartWith(propertyName, subString));
    return false;
  }

  public boolean notStartsWith(Function<T, String> getter, String subString) {
    return notStartsWith(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean notStartsWith(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (!value.startsWith(subString)) {
      return true;
    }

    add(new MustNotStartWith(propertyName, subString));
    return false;
  }

  public boolean endsWith(Function<T, String> getter, String subString) {
    return endsWith(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean endsWith(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (value.endsWith(subString)) {
      return true;
    }

    add(new MustEndWith(propertyName, subString));
    return false;
  }

  public boolean notEndsWith(Function<T, String> getter, String subString) {
    return notEndsWith(getValue(getter), subString, getPropertyName(getter));
  }

  public boolean notEndsWith(String value, String subString, String propertyName) {
    Preconditions.checkArgument(subString != null && !subString.isEmpty(), "subString required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (!value.endsWith(subString)) {
      return true;
    }

    add(new MustNotEndWith(propertyName, subString));
    return false;
  }

  public boolean hasText(Function<T, String> getter) {
    return hasText(getValue(getter), getPropertyName(getter));
  }

  public boolean hasText(String value, String propertyName) {
    if (StringUtils.hasText(value)) {
      return true;
    }

    add(new MustHaveText(propertyName));
    return false;
  }

  public boolean lengthLe(Function<T, String> getter, int boundary) {
    return lengthLe(getValue(getter), boundary, getPropertyName(getter));
  }

  public boolean lengthLe(String value, int boundary, String propertyName) {
    Preconditions.checkArgument(boundary >= 0, "boundary must be non-negative");
    int length = value == null ? 0 : value.length();
    if (length <= boundary) {
      return true;
    }

    add(new LengthMustBeLessOrEqual(propertyName, boundary));
    return false;
  }

  public boolean lengthLess(Function<T, String> getter, int boundary) {
    return lengthLess(getValue(getter), boundary, getPropertyName(getter));
  }

  public boolean lengthLess(String value, int boundary, String propertyName) {
    Preconditions.checkArgument(boundary > 0, "boundary must be positive");
    int length = value == null ? 0 : value.length();
    if (length < boundary) {
      return true;
    }

    add(new LengthMustBeLess(propertyName, boundary));
    return false;
  }

  public boolean lengthGe(Function<T, String> getter, int boundary) {
    return lengthGe(getValue(getter), boundary, getPropertyName(getter));
  }

  public boolean lengthGe(String value, int boundary, String propertyName) {
    Preconditions.checkArgument(boundary >= 0, "boundary must be non-negative");
    int length = value == null ? 0 : value.length();
    if (length >= boundary) {
      return true;
    }

    add(new LengthMustBeGreaterOrEqual(propertyName, boundary));
    return false;
  }

  public boolean lengthGreater(Function<T, String> getter, int boundary) {
    return lengthGreater(getValue(getter), boundary, getPropertyName(getter));
  }

  public boolean lengthGreater(String value, int boundary, String propertyName) {
    Preconditions.checkArgument(boundary >= 0, "boundary must be non-negative");
    int length = value == null ? 0 : value.length();
    if (length > boundary) {
      return true;
    }

    add(new LengthMustBeGreater(propertyName, boundary));
    return false;
  }

  public boolean empty(Function<T, Collection<?>> getter) {
    return empty(getValue(getter), getPropertyName(getter));
  }

  public boolean empty(Collection<?> value, String propertyName) {
    if (CollectionUtils.isEmpty(value)) {
      return true;
    }

    add(new MustBeEmpty(propertyName));
    return false;
  }

  public boolean notEmpty(Function<T, Collection<?>> getter) {
    return notEmpty(getValue(getter), getPropertyName(getter));
  }

  public boolean notEmpty(Collection<?> value, String propertyName) {
    if (!CollectionUtils.isEmpty(value)) {
      return true;
    }

    add(new MustNotBeEmpty(propertyName));
    return false;
  }

  public boolean validEmail(Function<T, String> getter) {
    return validEmail(getValue(getter), getPropertyName(getter));
  }

  public boolean validEmail(String value, String propertyName) {
    if (isValidEmail(value)) {
      return true;
    }

    add(new MustBeValidEmail(propertyName));
    return false;
  }

  /**
   * Checks if email is valid.
   *
   * <p>ATTENTION: In this implementation we cut several corners in order to simplify code. We're
   * ignoring several edge cases which seem to be invalid for human eye, while according to
   * specification these are valid cases. Method will incorrectly report as <b>invalid</b>, while
   * these are <b>valid</b> emails.
   */
  public static boolean isValidEmail(String email) {
    // Method will incorrectly report as <b>invalid</b>, while these are
    // <b>valid</b>
    // emails:
    // <pre>
    // admin@mailserver1
    // " "@example.org
    // "very.(),:;<>[]\".VERY.\"very@\\ \"very\".unusual"@strange.example.com
    // "postmaster@[IPv6:2001:0db8:85a3:0000:0000:8a2e:0370:7334]"
    // </pre>
    // Method will incorrectly report as <b>valid</b>, while this is <b>invalid</b>
    // email:
    // <pre>
    // 1234567890123456789012345678901234567890123456789012345678901234+x@example.com
    // </pre>

    if (email == null) {
      return false;
    }
    return EMAIL_REGEXP.test(email);
  }

  /**
   * Validate if text matches given predicate. It is envisioned to be used in conjunction with
   * result of {@link Pattern#asMatchPredicate()}, but you can supply anything that impl {@link
   * Predicate}
   *
   * @param getter will be used to determine property name
   * @param matcher will test if value is valid
   * @param messageCode message code for cases when value is invalid. Since pattern example could be
   *     language-sensitive, we're requiring messageCode to be provided here. If you don't want to
   *     provide custom, you can use default one {@link MustMatchPattern#MESSAGE_CODE} but it is
   *     discouraged to do so as it doesn't not explain to the user what format is expected
   * @return true if value is valid, or false otherwise
   */
  public boolean matches(
      Function<T, String> getter, Predicate<String> matcher, String messageCode) {
    return matches(getValue(getter), matcher, messageCode, getPropertyName(getter));
  }

  public boolean matches(
      String value, Predicate<String> matcher, String messageCode, String propertyName) {

    Preconditions.checkArgument(matcher != null, "matcher required");
    Preconditions.checkArgument(StringUtils.hasText(messageCode), "messageCode required");

    if (!notNull(value, propertyName)) {
      return false;
    }

    if (matcher.test(value)) {
      return true;
    }

    add(new MustMatchPattern(propertyName, messageCode));
    return false;
  }

  public <V extends Comparable<V>> boolean future(Function<T, V> getter) {
    return future(getValue(getter), getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean future(V value, String propertyName) {
    if (!notNull(value, propertyName)) {
      return false;
    }

    V boundary = buildTemporalBoundaryMatchingTypeOfValidationSubject(value);

    if (boundary.compareTo(value) < 0) {
      return true;
    }

    add(new MustBeInFuture(propertyName));
    return false;
  }

  public <V extends Comparable<V>> boolean futureOrPresent(Function<T, V> getter) {
    return futureOrPresent(getValue(getter), getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean futureOrPresent(V value, String propertyName) {
    if (!notNull(value, propertyName)) {
      return false;
    }

    V boundary = buildTemporalBoundaryMatchingTypeOfValidationSubject(value);

    if (boundary.compareTo(value) <= 0) {
      return true;
    }

    add(new MustBeInFutureOrPresent(propertyName));
    return false;
  }

  public <V extends Comparable<V>> boolean past(Function<T, V> getter) {
    return past(getValue(getter), getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean past(V value, String propertyName) {
    if (!notNull(value, propertyName)) {
      return false;
    }

    V boundary = buildTemporalBoundaryMatchingTypeOfValidationSubject(value);

    if (value.compareTo(boundary) < 0) {
      return true;
    }

    add(new MustBeInPast(propertyName));
    return false;
  }

  public <V extends Comparable<V>> boolean pastOrPresent(Function<T, V> getter) {
    return pastOrPresent(getValue(getter), getPropertyName(getter));
  }

  public <V extends Comparable<V>> boolean pastOrPresent(V value, String propertyName) {
    if (!notNull(value, propertyName)) {
      return false;
    }

    V boundary = buildTemporalBoundaryMatchingTypeOfValidationSubject(value);

    if (value.compareTo(boundary) <= 0) {
      return true;
    }

    add(new MustBeInPastOrPresent(propertyName));
    return false;
  }

  @SuppressWarnings("unchecked")
  protected <V extends Comparable<V>> V buildTemporalBoundaryMatchingTypeOfValidationSubject(
      V value) {
    Preconditions.checkArgument(value != null, "value required");
    Function<NowResolver, V> builder = getTemporalBuilderOfType(value.getClass());
    return builder.apply(nowResolver);
  }

  @SuppressWarnings({"unchecked"})
  protected <V extends Comparable<V>> Function<NowResolver, V> getTemporalBuilderOfType(
      Class<V> clazz) {
    for (Entry<Class<?>, Function<NowResolver, Comparable<?>>> entry :
        ALLOWED_TEMPORAL_TYPES.entrySet()) {
      if (entry.getKey().isAssignableFrom(clazz)) {
        // NOTE: if you know how to get rid of this workaround -- please let me know
        Object raw = entry.getValue();
        return (Function<NowResolver, V>) raw;
      }
    }

    throw new IllegalArgumentException(
        "Value class " + clazz + " is not one of the allowed temporal types");
  }
}
