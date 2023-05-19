/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.easycrud.api.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.QueryToNativeSqlCompiler;
import org.summerb.easycrud.api.query.restrictions.BooleanEqRestriction;
import org.summerb.easycrud.api.query.restrictions.IsNullRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberEqRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberGreaterOrEqualRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberLessOrEqualsRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringEqRestriction;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringStartsWithRestriction;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.tools.EasyCrudDtoUtils;

/**
 * This class is used to build very simple queries to {@link EasyCrudService} (nothing fancy -- no
 * aggregation, etc).
 *
 * <p>I.e.: <code>
 * Query.n().eq(HasId.FN_ID, 123)
 * </code> It's also capable of evaluating if any in-memory DTO matches this query.
 *
 * <p>Each impl of {@link EasyCrudDao} supposed to be injected with it's own impl of {@link
 * QueryToNativeSqlCompiler} that can convert this abstracted Query to query native to actual data
 * source.
 *
 * @author sergey.karpushin
 */
public class Query implements Serializable {
  private static final long serialVersionUID = 3434200920910840380L;

  protected List<Restriction<PropertyAccessor>> restrictions =
      new LinkedList<Restriction<PropertyAccessor>>();

  /**
   * Shortcut for creating new instance of this class
   *
   * @return initialized (and empty) Query instance
   */
  public static Query n() {
    return new Query();
  }

  public boolean isMeet(PropertyAccessor subjectValue) {
    for (Restriction<PropertyAccessor> r : restrictions) {
      if (!r.isMeet(subjectValue)) {
        return false;
      }
    }
    return true;
  }

  public List<Restriction<PropertyAccessor>> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<Restriction<PropertyAccessor>> restrictions) {
    this.restrictions = restrictions;
  }

  public Query or(Query... subqueries) {
    restrictions.add(new DisjunctionCondition(subqueries));
    return this;
  }

  public Query isNull(String fieldName) {
    restrictions.add(new FieldCondition(fieldName, new IsNullRestriction()));
    return this;
  }

  public Query isNotNull(String fieldName) {
    IsNullRestriction r = new IsNullRestriction();
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query eq(String fieldName, String value) {
    restrictions.add(new FieldCondition(fieldName, new StringEqRestriction(value)));
    return this;
  }

  public Query ne(String fieldName, String value) {
    StringEqRestriction r = new StringEqRestriction(value);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query lengthBetween(String fieldName, int minLength, int maxLength) {
    restrictions.add(
        new FieldCondition(fieldName, new StringLengthBetweenRestriction(minLength, maxLength)));
    return this;
  }

  public Query in(String fieldName, String... values) {
    restrictions.add(new FieldCondition(fieldName, new StringOneOfRestriction(values)));
    return this;
  }

  public Query notIn(String fieldName, String... values) {
    StringOneOfRestriction notIn = new StringOneOfRestriction(values);
    notIn.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, notIn));
    return this;
  }

  public Query contains(String fieldName, String value) {
    restrictions.add(new FieldCondition(fieldName, new StringContainsRestriction(value)));
    return this;
  }

  public Query notContains(String fieldName, String value) {
    StringContainsRestriction r = new StringContainsRestriction(value);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query isTrue(String fieldName) {
    restrictions.add(new FieldCondition(fieldName, new BooleanEqRestriction(true)));
    return this;
  }

  public Query isFalse(String fieldName) {
    restrictions.add(new FieldCondition(fieldName, new BooleanEqRestriction(false)));
    return this;
  }

  public Query ne(String fieldName, boolean value) {
    BooleanEqRestriction r = new BooleanEqRestriction(value);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query eq(String fieldName, long value) {
    restrictions.add(new FieldCondition(fieldName, new NumberEqRestriction(value)));
    return this;
  }

  public Query ne(String fieldName, long value) {
    NumberEqRestriction r = new NumberEqRestriction(value);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query in(String fieldName, Long... values) {
    restrictions.add(new FieldCondition(fieldName, new NumberOneOfRestriction(values)));
    return this;
  }

  public Query notIn(String fieldName, Long... values) {
    NumberOneOfRestriction r = new NumberOneOfRestriction(values);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query between(String fieldName, long lowerBound, long upperBound) {
    restrictions.add(
        new FieldCondition(fieldName, new NumberBetweenRestriction(lowerBound, upperBound)));
    return this;
  }

  public Query between(String fieldName, String lowerBound, String upperBound) {
    restrictions.add(
        new FieldCondition(fieldName, new StringBetweenRestriction(lowerBound, upperBound)));
    return this;
  }

  public Query notBetween(String fieldName, long lowerBound, long upperBound) {
    NumberBetweenRestriction r = new NumberBetweenRestriction(lowerBound, upperBound);
    r.setNegative(true);
    restrictions.add(new FieldCondition(fieldName, r));
    return this;
  }

  public Query ge(String fieldName, long value) {
    restrictions.add(new FieldCondition(fieldName, new NumberGreaterOrEqualRestriction(value)));
    return this;
  }

  public Query le(String fieldName, long value) {
    getRestrictions().add(new FieldCondition(fieldName, new NumberLessOrEqualsRestriction(value)));
    return this;
  }

  public Query notLe(String fieldName, long value) {
    getRestrictions()
        .add(new FieldCondition(fieldName, new NumberLessOrEqualsRestriction(value).asNegative()));
    return this;
  }

  public Query startsWith(String fieldName, String value) {
    getRestrictions().add(new FieldCondition(fieldName, new StringStartsWithRestriction(value)));
    return this;
  }

  public Query notStartsWith(String fieldName, String value) {
    getRestrictions()
        .add(new FieldCondition(fieldName, new StringStartsWithRestriction(value).asNegative()));
    return this;
  }

  public Query inStrings(String fieldName, Collection<String> values) {
    getRestrictions().add(new FieldCondition(fieldName, new StringOneOfRestriction(asSet(values))));
    return this;
  }

  public Query inIdsStrings(String fieldName, Iterable<? extends HasId<String>> rows) {
    return inStrings(fieldName, EasyCrudDtoUtils.enumerateIds(rows));
  }

  public Query notInIdsStrings(String fieldName, Iterable<? extends HasId<String>> rows) {
    return notInStrings(fieldName, EasyCrudDtoUtils.enumerateIds(rows));
  }

  public Query notInStrings(String fieldName, Collection<String> values) {
    StringOneOfRestriction notIn = new StringOneOfRestriction(asSet(values));
    notIn.setNegative(true);
    getRestrictions().add(new FieldCondition(fieldName, notIn));
    return this;
  }

  public Query inLongs(String fieldName, Collection<Long> values) {
    getRestrictions().add(new FieldCondition(fieldName, new NumberOneOfRestriction(asSet(values))));
    return this;
  }

  public Query notInLongs(String fieldName, Collection<Long> values) {
    NumberOneOfRestriction notIn = new NumberOneOfRestriction(asSet(values));
    notIn.setNegative(true);
    getRestrictions().add(new FieldCondition(fieldName, notIn));
    return this;
  }

  public Query inIdsLongs(String fieldName, Iterable<? extends HasId<Long>> rows) {
    return inLongs(fieldName, EasyCrudDtoUtils.enumerateIds(rows));
  }

  public Query notInIdsLongs(String fieldName, Iterable<? extends HasId<Long>> rows) {
    return notInLongs(fieldName, EasyCrudDtoUtils.enumerateIds(rows));
  }

  protected <T> Set<T> asSet(Collection<T> values) {
    if (values instanceof Set) {
      return (Set<T>) values;
    }

    return new HashSet<>(values);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((restrictions == null) ? 0 : restrictions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Query other = (Query) obj;
    if (restrictions == null) {
      if (other.restrictions != null) return false;
    } else if (!restrictions.equals(other.restrictions)) return false;
    return true;
  }
}
