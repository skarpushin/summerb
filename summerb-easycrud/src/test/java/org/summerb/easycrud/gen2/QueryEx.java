package org.summerb.easycrud.gen2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.gen2.restrictions.Between;
import org.summerb.easycrud.gen2.restrictions.Empty;
import org.summerb.easycrud.gen2.restrictions.Equals;
import org.summerb.easycrud.gen2.restrictions.HasText;
import org.summerb.easycrud.gen2.restrictions.In;
import org.summerb.easycrud.gen2.restrictions.IsNull;
import org.summerb.easycrud.gen2.restrictions.IsTrue;
import org.summerb.easycrud.gen2.restrictions.LengthLess;
import org.summerb.easycrud.gen2.restrictions.Less;
import org.summerb.easycrud.gen2.restrictions.Like;
import org.summerb.easycrud.gen2.restrictions.RestrictionEx;
import org.summerb.easycrud.gen2.restrictions.StringLengthBetween;
import org.summerb.easycrud.query.ConditionEx;
import org.summerb.easycrud.query.FieldConditionEx;
import org.summerb.methodCapturers.PropertyNameObtainer;

import com.google.common.base.Preconditions;

/**
 * A lightweight and simple way for building queries for {@link EasyCrudService}. It provides usual
 * conditions, nothing fancy (no aggregation, etc). If you need to build complex queries please
 * consider other options. But usually QueryEx will provide sufficient facilities for querying rows.
 *
 * <p>It provides you with ability to specify field names two ways: a) Method references (it uses
 * ByteBuddy under the hood to extract field names) and b) using string literals.
 *
 * <p>It is not recommended to specify field names as string literals because then you loose all
 * power of static code analysis, compiler defense against typos and IDE features like call
 * hierarchy analysis and renaming
 *
 * @author Sergey Karpushin
 * @param <T> type of Row for which this query is being built
 */
public class QueryEx<T> {

  protected final PropertyNameObtainer<T> propertyNameObtainer;
  protected final List<ConditionEx> conditions = new ArrayList<>();

  public QueryEx(PropertyNameObtainer<T> fieldNameCapturer) {
    this.propertyNameObtainer = fieldNameCapturer;
  }

  public List<ConditionEx> getConditions() {
    return conditions;
  }

  public void addCondition(ConditionEx condition) {
    Preconditions.checkArgument(condition != null, "condition required");
    conditions.add(condition);
  }

  public void addRestriction(Function<T, Object> getter, RestrictionEx restriction) {
    String fieldName = obtainFieldName(getter);
    addRestriction(fieldName, restriction);
  }

  protected String obtainFieldName(Function<T, Object> getter) {
    Preconditions.checkState(propertyNameObtainer != null, "propertyNameObtainer is not provided");
    Preconditions.checkArgument(getter != null, "getter required");
    return propertyNameObtainer.obtainFrom(getter);
  }

  public void addRestriction(String fieldName, RestrictionEx restriction) {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");
    Preconditions.checkArgument(restriction != null, "restriction required");
    addCondition(new FieldConditionEx(fieldName, restriction));
  }

  public QueryEx<T> or(Collection<QueryEx<T>> disjunctions) {
    addCondition(new DisjunctionConditionEx<>(disjunctions));
    return this;
  }

  public QueryEx<T> isNull(Function<T, Object> getter) {
    isNull(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> notNull(Function<T, Object> getter) {
    notNull(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> isTrue(Function<T, Object> getter) {
    isTrue(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> isFalse(Function<T, Object> getter) {
    isFalse(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> eq(Function<T, Object> getter, Object value) {
    eq(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> ne(Function<T, Object> getter, Object value) {
    ne(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> less(Function<T, Object> getter, Object value) {
    less(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> le(Function<T, Object> getter, Object value) {
    le(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> greater(Function<T, Object> getter, Object value) {
    greater(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> ge(Function<T, Object> getter, Object value) {
    ge(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> in(Function<T, Object> getter, Collection<Object> values) {
    in(obtainFieldName(getter), values);
    return this;
  }

  public QueryEx<T> notIn(Function<T, Object> getter, Collection<Object> values) {
    notIn(obtainFieldName(getter), values);
    return this;
  }

  public <A> QueryEx<T> between(Function<T, Object> getter, A lowerBoundary, A upperBoundary) {
    between(obtainFieldName(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public <A> QueryEx<T> notBetween(Function<T, Object> getter, A lowerBoundary, A upperBoundary) {
    notBetween(obtainFieldName(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public QueryEx<T> stringLengthBetween(
      Function<T, Object> getter, Number lowerBoundary, Number upperBoundary) {
    stringLengthBetween(obtainFieldName(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public QueryEx<T> stringLengthNotBetween(
      Function<T, Object> getter, Number lowerBoundary, Number upperBoundary) {
    stringLengthNotBetween(obtainFieldName(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public QueryEx<T> like(Function<T, Object> getter, String subString) {
    like(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> notLike(Function<T, Object> getter, String subString) {
    notLike(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> contains(Function<T, Object> getter, String subString) {
    contains(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> notContains(Function<T, Object> getter, String subString) {
    notContains(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> startsWith(Function<T, Object> getter, String subString) {
    startsWith(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> notStartsWith(Function<T, Object> getter, String subString) {
    notStartsWith(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> endsWith(Function<T, Object> getter, String subString) {
    endsWith(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> notEndsWith(Function<T, Object> getter, String subString) {
    notEndsWith(obtainFieldName(getter), subString);
    return this;
  }

  public QueryEx<T> empty(Function<T, Object> getter) {
    empty(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> notEmpty(Function<T, Object> getter) {
    notEmpty(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> hasText(Function<T, Object> getter) {
    hasText(obtainFieldName(getter));
    return this;
  }

  public QueryEx<T> lengthLe(Function<T, Object> getter, int value) {
    lengthLe(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> lengthLess(Function<T, Object> getter, int value) {
    lengthLess(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> lengthGe(Function<T, Object> getter, int value) {
    lengthGe(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> lengthGreater(Function<T, Object> getter, int value) {
    lengthGreater(obtainFieldName(getter), value);
    return this;
  }

  public QueryEx<T> isNull(String fieldName) {
    addRestriction(fieldName, new IsNull());
    return this;
  }

  public QueryEx<T> notNull(String fieldName) {
    addRestriction(fieldName, new IsNull().not());
    return this;
  }

  public QueryEx<T> isTrue(String fieldName) {
    addRestriction(fieldName, new IsTrue());
    return this;
  }

  public QueryEx<T> isFalse(String fieldName) {
    addRestriction(fieldName, new IsTrue().not());
    return this;
  }

  public QueryEx<T> eq(String fieldName, Object value) {
    if (value == null) {
      isNull(fieldName);
    } else {
      addRestriction(fieldName, new Equals(value));
    }
    return this;
  }

  public QueryEx<T> ne(String fieldName, Object value) {
    if (value == null) {
      notNull(fieldName);
    } else {
      addRestriction(fieldName, new Equals(value).not());
    }
    return this;
  }

  public QueryEx<T> less(String fieldName, Object value) {
    addRestriction(fieldName, new Less(value, false));
    return this;
  }

  public QueryEx<T> le(String fieldName, Object value) {
    addRestriction(fieldName, new Less(value, true));
    return this;
  }

  public QueryEx<T> greater(String fieldName, Object value) {
    addRestriction(fieldName, new Less(value, true).not());
    return this;
  }

  public QueryEx<T> ge(String fieldName, Object value) {
    addRestriction(fieldName, new Less(value, false).not());
    return this;
  }

  public QueryEx<T> in(String fieldName, Collection<Object> values) {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(values), "Non empty collection expected");
    Collection<Object> actualValues = extractIdsIfAny(values);
    addRestriction(fieldName, new In(actualValues));
    return this;
  }

  protected Collection<Object> extractIdsIfAny(Collection<Object> values) {
    Object firstValue = getFirstValue(values);
    if (firstValue instanceof HasId<?>) {
      return values.stream().map(x -> ((HasId<?>) x).getId()).collect(Collectors.toSet());
    }
    return values;
  }

  protected Object getFirstValue(Collection<Object> values) {
    if (values instanceof List<?>) {
      return ((List<?>) values).get(0);
    }
    return values.iterator().next();
  }

  public QueryEx<T> notIn(String fieldName, Collection<Object> values) {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(values), "Non empty collection expected");
    Collection<Object> actualValues = extractIdsIfAny(values);
    addRestriction(fieldName, new In(actualValues).not());
    return this;
  }

  public <A> QueryEx<T> between(String fieldName, A lowerBoundary, A upperBoundary) {
    addRestriction(fieldName, new Between(lowerBoundary, upperBoundary));
    return this;
  }

  public <A> QueryEx<T> notBetween(String fieldName, A lowerBoundary, A upperBoundary) {
    addRestriction(fieldName, new Between(lowerBoundary, upperBoundary).not());
    return this;
  }

  public QueryEx<T> stringLengthBetween(
      String fieldName, Number lowerBoundary, Number upperBoundary) {
    addRestriction(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary));
    return this;
  }

  public QueryEx<T> stringLengthNotBetween(
      String fieldName, Number lowerBoundary, Number upperBoundary) {
    addRestriction(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary).not());
    return this;
  }

  public QueryEx<T> like(String fieldName, String likeExpression) {
    addRestriction(fieldName, new Like(likeExpression));
    return this;
  }

  public QueryEx<T> notLike(String fieldName, String likeExpression) {
    addRestriction(fieldName, new Like(likeExpression).not());
    return this;
  }

  public QueryEx<T> contains(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, true, true));
    return this;
  }

  public QueryEx<T> notContains(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, true, true).not());
    return this;
  }

  public QueryEx<T> startsWith(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, false, true));
    return this;
  }

  public QueryEx<T> notStartsWith(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, false, true).not());
    return this;
  }

  public QueryEx<T> endsWith(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, true, false));
    return this;
  }

  public QueryEx<T> notEndsWith(String fieldName, String subString) {
    addRestriction(fieldName, new Like(subString, true, false).not());
    return this;
  }

  public QueryEx<T> empty(String fieldName) {
    addRestriction(fieldName, new Empty());
    return this;
  }

  public QueryEx<T> notEmpty(String fieldName) {
    addRestriction(fieldName, new Empty().not());
    return this;
  }

  public QueryEx<T> hasText(String fieldName) {
    addRestriction(fieldName, new HasText());
    return this;
  }

  public QueryEx<T> lengthLe(String fieldName, int value) {
    addRestriction(fieldName, new LengthLess(value, true));
    return this;
  }

  public QueryEx<T> lengthLess(String fieldName, int value) {
    addRestriction(fieldName, new LengthLess(value, false));
    return this;
  }

  public QueryEx<T> lengthGe(String fieldName, int value) {
    addRestriction(fieldName, new LengthLess(value, false).not());
    return this;
  }

  public QueryEx<T> lengthGreater(String fieldName, int value) {
    addRestriction(fieldName, new LengthLess(value, true).not());
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    QueryEx<T> other = (QueryEx<T>) obj;
    if (conditions == null) {
      if (other.conditions != null) {
        return false;
      }
    } else if (!conditions.equals(other.conditions)) {
      return false;
    }
    return true;
  }
}
