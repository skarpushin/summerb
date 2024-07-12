package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.query.restrictions.Between;
import org.summerb.easycrud.api.query.restrictions.Empty;
import org.summerb.easycrud.api.query.restrictions.Equals;
import org.summerb.easycrud.api.query.restrictions.In;
import org.summerb.easycrud.api.query.restrictions.IsNull;
import org.summerb.easycrud.api.query.restrictions.Less;
import org.summerb.easycrud.api.query.restrictions.Like;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetween;
import org.summerb.easycrud.api.query.restrictions.StringLengthLess;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.tools.EasyCrudDtoUtils;
import org.summerb.methodCapturers.PropertyNameObtainer;

public class QueryShortcuts<TRow, TypeOfThis extends QueryShortcuts<TRow, TypeOfThis>>
    extends QueryConditions {

  protected final PropertyNameObtainer<TRow> propertyNameObtainer;

  public QueryShortcuts() {
    this.propertyNameObtainer = null;
  }

  public QueryShortcuts(PropertyNameObtainer<TRow> propertyNameObtainer) {
    super();
    Preconditions.checkArgument(propertyNameObtainer != null, "propertyNameObtainer required");
    this.propertyNameObtainer = propertyNameObtainer;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis or(List<? extends QueryConditions> disjunctions) {
    add(new DisjunctionCondition(disjunctions));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis or(QueryConditions... disjunctions) {
    add(new DisjunctionCondition(Arrays.asList(disjunctions)));
    return (TypeOfThis) this;
  }

  public void add(Function<TRow, ?> getter, Restriction restriction) {
    String fieldName = name(getter);
    add(fieldName, restriction);
  }

  /**
   * This is a short convenient method for obtaining field name from getter
   *
   * @param getter to obtain name from
   * @return field name
   */
  public String name(Function<TRow, ?> getter) {
    Preconditions.checkState(propertyNameObtainer != null, "propertyNameObtainer is not provided");
    Preconditions.checkArgument(getter != null, "getter required");
    return propertyNameObtainer.obtainFrom(getter);
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isNull(Function<TRow, ?> getter) {
    isNull(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isNotNull(Function<TRow, ?> getter) {
    isNotNull(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isTrue(Function<TRow, Boolean> getter) {
    isTrue(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isFalse(Function<TRow, Boolean> getter) {
    isFalse(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis eq(Function<TRow, T> getter, T value) {
    eq(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis ne(Function<TRow, T> getter, T value) {
    ne(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis less(Function<TRow, T> getter, T value) {
    less(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis le(Function<TRow, T> getter, T value) {
    le(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis greater(Function<TRow, T> getter, T value) {
    greater(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis ge(Function<TRow, T> getter, T value) {
    ge(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis in(Function<TRow, T> getter, Collection<T> values) {
    in(name(getter), values);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis inIds(Function<TRow, T> getter, Collection<? extends HasId<T>> values) {
    in(name(getter), EasyCrudDtoUtils.enumerateIds(values));
    return (TypeOfThis) this;
  }

  /**
   * Adds IN constraint to field with values provided in var-arg parameter values
   *
   * <p>NOTE: we're not using overload approach for method name because it confuses Eclipse big
   * time. So we have to go with a longer name "inArr" instead of just "in"
   *
   * @param <T> type of field
   * @param getter for getting value from DTO (method reference that is used to obtain field name)
   * @param values values for IN expression
   * @return this
   */
  @SuppressWarnings("unchecked")
  public <T> TypeOfThis inArr(Function<TRow, T> getter, T... values) {
    return in(getter, Arrays.asList(values));
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis notIn(Function<TRow, T> getter, Collection<? extends T> values) {
    notIn(name(getter), values);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis notInIds(Function<TRow, T> getter, Collection<? extends HasId<T>> values) {
    notIn(name(getter), EasyCrudDtoUtils.enumerateIds(values));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <T> TypeOfThis notInArr(Function<TRow, T> getter, T... values) {
    return notIn(getter, Arrays.asList(values));
  }

  @SuppressWarnings("unchecked")
  public <A extends Comparable<A>> TypeOfThis between(
      Function<TRow, A> getter, A lowerBoundary, A upperBoundary) {
    between(name(getter), lowerBoundary, upperBoundary);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <A extends Comparable<A>> TypeOfThis notBetween(
      Function<TRow, A> getter, A lowerBoundary, A upperBoundary) {
    notBetween(name(getter), lowerBoundary, upperBoundary);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis stringLengthBetween(
      Function<TRow, String> getter, int lowerBoundary, int upperBoundary) {
    stringLengthBetween(name(getter), lowerBoundary, upperBoundary);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis stringLengthNotBetween(
      Function<TRow, String> getter, int lowerBoundary, int upperBoundary) {
    stringLengthNotBetween(name(getter), lowerBoundary, upperBoundary);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis like(Function<TRow, String> getter, String subString) {
    like(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notLike(Function<TRow, String> getter, String subString) {
    notLike(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis contains(Function<TRow, String> getter, String subString) {
    contains(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notContains(Function<TRow, String> getter, String subString) {
    notContains(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis startsWith(Function<TRow, String> getter, String subString) {
    startsWith(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notStartsWith(Function<TRow, String> getter, String subString) {
    notStartsWith(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis endsWith(Function<TRow, String> getter, String subString) {
    endsWith(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notEndsWith(Function<TRow, String> getter, String subString) {
    notEndsWith(name(getter), subString);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis empty(Function<TRow, Object> getter) {
    empty(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notEmpty(Function<TRow, Object> getter) {
    notEmpty(name(getter));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthLe(Function<TRow, Object> getter, int value) {
    lengthLe(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthLess(Function<TRow, Object> getter, int value) {
    lengthLess(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthGe(Function<TRow, Object> getter, int value) {
    lengthGe(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthGreater(Function<TRow, Object> getter, int value) {
    lengthGreater(name(getter), value);
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isNull(String fieldName) {
    add(fieldName, new IsNull());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isNotNull(String fieldName) {
    add(fieldName, new IsNull().not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isTrue(String fieldName) {
    add(fieldName, new Equals(true));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis isFalse(String fieldName) {
    add(fieldName, new Equals(false));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis eq(String fieldName, Object value) {
    if (value == null) {
      isNull(fieldName);
    } else {
      add(fieldName, new Equals(value));
    }
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis ne(String fieldName, Object value) {
    if (value == null) {
      isNotNull(fieldName);
    } else {
      add(fieldName, new Equals(value).not());
    }
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis less(String fieldName, Object value) {
    add(fieldName, new Less(value, false));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis le(String fieldName, Object value) {
    add(fieldName, new Less(value, true));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis greater(String fieldName, Object value) {
    add(fieldName, new Less(value, true).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis ge(String fieldName, Object value) {
    add(fieldName, new Less(value, false).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis in(String fieldName, Collection<?> values) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(values), "'in' constraint requires non-empty collection");

    if (values.size() == 1) {
      add(fieldName, new Equals(values.iterator().next()));
    } else {
      add(fieldName, new In(values));
    }
    return (TypeOfThis) this;
  }

  public TypeOfThis in(String fieldName, Object... values) {
    return in(fieldName, Arrays.asList(values));
  }

  public TypeOfThis notIn(String fieldName, Object... values) {
    return notIn(fieldName, Arrays.asList(values));
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notIn(String fieldName, Collection<?> values) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(values), "'notIn' constraint requires non-empty collection");

    if (values.size() == 1) {
      add(fieldName, new Equals(values.iterator().next()).not());
    } else {
      add(fieldName, new In(values).not());
    }
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <A extends Comparable<A>> TypeOfThis between(
      String fieldName, A lowerBoundary, A upperBoundary) {
    if (lowerBoundary.compareTo(upperBoundary) == 0) {
      add(fieldName, new Equals(lowerBoundary));
    } else {
      add(fieldName, new Between(lowerBoundary, upperBoundary));
    }
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public <A extends Comparable<A>> TypeOfThis notBetween(
      String fieldName, A lowerBoundary, A upperBoundary) {
    if (lowerBoundary.compareTo(upperBoundary) == 0) {
      add(fieldName, new Equals(lowerBoundary).not());
    } else {
      add(fieldName, new Between(lowerBoundary, upperBoundary).not());
    }
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis stringLengthBetween(String fieldName, int lowerBoundary, int upperBoundary) {
    add(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis stringLengthNotBetween(String fieldName, int lowerBoundary, int upperBoundary) {
    add(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis like(String fieldName, String likeExpression) {
    add(fieldName, new Like(likeExpression));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notLike(String fieldName, String likeExpression) {
    add(fieldName, new Like(likeExpression).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis contains(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, true));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notContains(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, true).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis startsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, false, true));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notStartsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, false, true).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis endsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, false));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notEndsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, false).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis empty(String fieldName) {
    add(fieldName, new Empty());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis notEmpty(String fieldName) {
    add(fieldName, new Empty().not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthLe(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, true));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthLess(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, false));
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthGe(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, false).not());
    return (TypeOfThis) this;
  }

  @SuppressWarnings("unchecked")
  public TypeOfThis lengthGreater(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, true).not());
    return (TypeOfThis) this;
  }
}
