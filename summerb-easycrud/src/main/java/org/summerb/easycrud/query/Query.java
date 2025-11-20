package org.summerb.easycrud.query;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.restrictions.Between;
import org.summerb.easycrud.query.restrictions.Empty;
import org.summerb.easycrud.query.restrictions.Equals;
import org.summerb.easycrud.query.restrictions.In;
import org.summerb.easycrud.query.restrictions.IsNull;
import org.summerb.easycrud.query.restrictions.Less;
import org.summerb.easycrud.query.restrictions.Like;
import org.summerb.easycrud.query.restrictions.StringLengthBetween;
import org.summerb.easycrud.query.restrictions.StringLengthLess;
import org.summerb.easycrud.query.restrictions.base.Restriction;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.scaffold.SqlQuery;
import org.summerb.easycrud.tools.EasyCrudDtoUtils;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * A lightweight and simple way for building queries for {@link EasyCrudService}. It provides usual
 * conditions, nothing fancy (no aggregation, etc.). If you need to build complex queries please
 * consider other options, i.e. use {@link SqlQuery}. But usually Query will provide sufficient
 * facilities for querying rows.
 *
 * <p>It provides you with ability to specify field names two ways: (a) Method references (it uses
 * ByteBuddy under the hood to extract field names) and (b) using string literals.
 *
 * <p>It is not recommended to specify field names as string literals because then you loose all
 * power of static code analysis, compiler defense against typos and IDE features like call
 * hierarchy analysis and renaming
 *
 * <p>Instead of using this class directly prefer to use {@link EasyCrudService#query()} which will
 * also allow you to chain actions like find, get, etc...
 *
 * @author Sergey Karpushin
 * @param <TRow> type of Row for which this query is being built
 */
public class Query<TId, TRow extends HasId<TId>> {
  protected final EasyCrudService<TId, TRow> service;

  /**
   * This alias is used only in conjunction with {@link JoinQuery} and is mostly needed to properly
   * parse {@link OrderBy} which came from front-ends (i.e. REST API). Also, if some table appear in
   * {@link JoinQuery} more than once, alias will be needed to distinguish between different
   * instances
   */
  protected String alias;

  protected final List<Condition> conditions = new LinkedList<>();

  protected boolean guaranteedToYieldEmptyResultset = false;

  public Query(EasyCrudService<TId, TRow> service) {
    Preconditions.checkArgument(service != null, "service required");
    this.service = service;
  }

  public Query(EasyCrudService<TId, TRow> service, String alias) {
    Preconditions.checkArgument(service != null, "service required");
    Preconditions.checkArgument(StringUtils.hasText(alias), "alias required");
    this.service = service;
    this.alias = alias;
  }

  public int count() {
    return service.count(this);
  }

  public TRow findOne() {
    return service.findOneByQuery(this);
  }

  public TRow getOne() {
    return service.getOneByQuery(this);
  }

  public TRow getFirst(OrderBy... orderBy) {
    return service.getFirstByQuery(this, orderBy);
  }

  public TRow findFirst(OrderBy... orderBy) {
    return service.findFirstByQuery(this, orderBy);
  }

  public PaginatedList<TRow> find(PagerParams pagerParams, OrderBy... orderBy) {
    return service.find(pagerParams, this, orderBy);
  }

  public List<TRow> findPage(PagerParams pagerParams, OrderBy... orderBy) {
    return service.findPage(pagerParams, this, orderBy);
  }

  public List<TRow> findAll(OrderBy... orderBy) {
    return service.findAll(this, orderBy);
  }

  public List<TRow> getAll(OrderBy... orderBy) {
    return service.getAll(this, orderBy);
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public void add(Condition condition) {
    Preconditions.checkArgument(condition != null, "condition required");
    conditions.add(condition);
  }

  public void add(String fieldName, Restriction restriction) {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");
    Preconditions.checkArgument(restriction != null, "restriction required");
    add(new FieldCondition(fieldName, restriction));
  }

  public boolean isEmpty() {
    return conditions.isEmpty();
  }

  public Query<TId, TRow> or(Query<TId, TRow> d1) {
    add(new DisjunctionCondition<>(Collections.singletonList(d1)));
    return this;
  }

  public Query<TId, TRow> or(Query<TId, TRow> d1, Query<TId, TRow> d2) {
    add(new DisjunctionCondition<>(Arrays.asList(d1, d2)));
    return this;
  }

  public Query<TId, TRow> or(Query<TId, TRow> d1, Query<TId, TRow> d2, Query<TId, TRow> d3) {
    add(new DisjunctionCondition<>(Arrays.asList(d1, d2, d3)));
    return this;
  }

  public Query<TId, TRow> or(
      Query<TId, TRow> d1, Query<TId, TRow> d2, Query<TId, TRow> d3, Query<TId, TRow> d4) {
    add(new DisjunctionCondition<>(Arrays.asList(d1, d2, d3, d4)));
    return this;
  }

  public Query<TId, TRow> or(
      Query<TId, TRow> d1,
      Query<TId, TRow> d2,
      Query<TId, TRow> d3,
      Query<TId, TRow> d4,
      Query<TId, TRow> d5) {
    add(new DisjunctionCondition<>(Arrays.asList(d1, d2, d3, d4, d5)));
    return this;
  }

  public Query<TId, TRow> or(List<Query<TId, TRow>> disjunctions) {
    add(new DisjunctionCondition<>(disjunctions));
    return this;
  }

  public void add(Function<TRow, ?> getter, Restriction restriction) {
    String fieldName = name(getter);
    add(fieldName, restriction);
  }

  /**
   * In some cases it possible for Query to know upfront that execution of such a query will yield
   * no results. I.e. in the case when `in` clause was added with empty collection. In such a case
   * we want to avoid executing the request because it will just throw exception, demanding IN
   * clause to be used together with non-empty collection. And while JDBC will throw such exception,
   * for SQL it is totally normal to have `IN SELECT ...`, where SELECT returns empty resultset. So
   * we want to also allow client code to be a bit cleaner so that it doesn't have to check if the
   * collection is empty before using `in` clause.
   */
  public boolean isGuaranteedToYieldEmptyResultset() {
    if (guaranteedToYieldEmptyResultset) {
      return true;
    }

    for (Condition condition : conditions) {
      if (!(condition instanceof DisjunctionCondition<?, ?> disj)) {
        continue;
      }
      if (disj.queries.stream().allMatch(Query::isGuaranteedToYieldEmptyResultset)) {
        return true;
      }
    }

    return false;
  }

  /**
   * This is a short convenient method for obtaining field name from getter
   *
   * @param getter to obtain name from
   * @return field name
   */
  public String name(Function<TRow, ?> getter) {
    return service.name(getter);
  }

  public Query<TId, TRow> isNull(Function<TRow, ?> getter) {
    isNull(name(getter));
    return this;
  }

  public Query<TId, TRow> isNotNull(Function<TRow, ?> getter) {
    isNotNull(name(getter));
    return this;
  }

  public Query<TId, TRow> isTrue(Function<TRow, Boolean> getter) {
    isTrue(name(getter));
    return this;
  }

  public Query<TId, TRow> isFalse(Function<TRow, Boolean> getter) {
    isFalse(name(getter));
    return this;
  }

  public <T> Query<TId, TRow> eq(Function<TRow, T> getter, T value) {
    eq(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> ne(Function<TRow, T> getter, T value) {
    ne(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> less(Function<TRow, T> getter, T value) {
    less(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> le(Function<TRow, T> getter, T value) {
    le(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> greater(Function<TRow, T> getter, T value) {
    greater(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> ge(Function<TRow, T> getter, T value) {
    ge(name(getter), value);
    return this;
  }

  public <T> Query<TId, TRow> in(Function<TRow, T> getter, Collection<T> values) {
    in(name(getter), values);
    return this;
  }

  public <T, TSource> Query<TId, TRow> in(
      Function<TRow, T> getter,
      Collection<TSource> sourceCollection,
      Function<TSource, T> valueExtractor) {
    in(name(getter), sourceCollection, valueExtractor);
    return this;
  }

  public <T> Query<TId, TRow> inIds(
      Function<TRow, T> getter, Collection<? extends HasId<T>> values) {
    in(name(getter), EasyCrudDtoUtils.enumerateIds(values));
    return this;
  }

  /**
   * Adds IN constraint to field with values provided in var-arg parameter values
   *
   * <p>NOTE: we're not using an overload approach for method name because it confuses Eclipse big
   * time. So we have to go with a longer name "inArr" instead of just "in"
   *
   * @param <T> type of field
   * @param getter for getting value from DTO (method reference that is used to obtain field name)
   * @param values values for IN expression
   * @return this
   */
  @SuppressWarnings("unchecked")
  public <T> Query<TId, TRow> inArr(Function<TRow, T> getter, T... values) {
    return in(getter, Arrays.asList(values));
  }

  public <T> Query<TId, TRow> notIn(Function<TRow, T> getter, Collection<? extends T> values) {
    notIn(name(getter), values);
    return this;
  }

  public <T, TSource> Query<TId, TRow> notIn(
      Function<TRow, T> getter,
      Collection<TSource> sourceCollection,
      Function<TSource, T> valueExtractor) {
    notIn(name(getter), sourceCollection, valueExtractor);
    return this;
  }

  public <T> Query<TId, TRow> notInIds(
      Function<TRow, T> getter, Collection<? extends HasId<T>> values) {
    notIn(name(getter), EasyCrudDtoUtils.enumerateIds(values));
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> Query<TId, TRow> notInArr(Function<TRow, T> getter, T... values) {
    return notIn(getter, Arrays.asList(values));
  }

  public <A extends Comparable<A>> Query<TId, TRow> between(
      Function<TRow, A> getter, A lowerBoundary, A upperBoundary) {
    between(name(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public <A extends Comparable<A>> Query<TId, TRow> notBetween(
      Function<TRow, A> getter, A lowerBoundary, A upperBoundary) {
    notBetween(name(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public Query<TId, TRow> stringLengthBetween(
      Function<TRow, String> getter, int lowerBoundary, int upperBoundary) {
    stringLengthBetween(name(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public Query<TId, TRow> stringLengthNotBetween(
      Function<TRow, String> getter, int lowerBoundary, int upperBoundary) {
    stringLengthNotBetween(name(getter), lowerBoundary, upperBoundary);
    return this;
  }

  public Query<TId, TRow> like(Function<TRow, String> getter, String subString) {
    like(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> notLike(Function<TRow, String> getter, String subString) {
    notLike(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> contains(Function<TRow, String> getter, String subString) {
    contains(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> notContains(Function<TRow, String> getter, String subString) {
    notContains(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> startsWith(Function<TRow, String> getter, String subString) {
    startsWith(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> notStartsWith(Function<TRow, String> getter, String subString) {
    notStartsWith(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> endsWith(Function<TRow, String> getter, String subString) {
    endsWith(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> notEndsWith(Function<TRow, String> getter, String subString) {
    notEndsWith(name(getter), subString);
    return this;
  }

  public Query<TId, TRow> empty(Function<TRow, Object> getter) {
    empty(name(getter));
    return this;
  }

  public Query<TId, TRow> notEmpty(Function<TRow, Object> getter) {
    notEmpty(name(getter));
    return this;
  }

  public Query<TId, TRow> lengthLe(Function<TRow, Object> getter, int value) {
    lengthLe(name(getter), value);
    return this;
  }

  public Query<TId, TRow> lengthLess(Function<TRow, Object> getter, int value) {
    lengthLess(name(getter), value);
    return this;
  }

  public Query<TId, TRow> lengthGe(Function<TRow, Object> getter, int value) {
    lengthGe(name(getter), value);
    return this;
  }

  public Query<TId, TRow> lengthGreater(Function<TRow, Object> getter, int value) {
    lengthGreater(name(getter), value);
    return this;
  }

  public Query<TId, TRow> isNull(String fieldName) {
    add(fieldName, new IsNull());
    return this;
  }

  public Query<TId, TRow> isNotNull(String fieldName) {
    add(fieldName, new IsNull().not());
    return this;
  }

  public Query<TId, TRow> isTrue(String fieldName) {
    add(fieldName, new Equals(true));
    return this;
  }

  public Query<TId, TRow> isFalse(String fieldName) {
    add(fieldName, new Equals(false));
    return this;
  }

  public Query<TId, TRow> eq(String fieldName, Object value) {
    if (value == null) {
      isNull(fieldName);
    } else {
      add(fieldName, new Equals(value));
    }
    return this;
  }

  public Query<TId, TRow> ne(String fieldName, Object value) {
    if (value == null) {
      isNotNull(fieldName);
    } else {
      add(fieldName, new Equals(value).not());
    }
    return this;
  }

  public Query<TId, TRow> less(String fieldName, Object value) {
    add(fieldName, new Less(value, false));
    return this;
  }

  public Query<TId, TRow> le(String fieldName, Object value) {
    add(fieldName, new Less(value, true));
    return this;
  }

  public Query<TId, TRow> greater(String fieldName, Object value) {
    add(fieldName, new Less(value, true).not());
    return this;
  }

  public Query<TId, TRow> ge(String fieldName, Object value) {
    add(fieldName, new Less(value, false).not());
    return this;
  }

  public <TSource> Query<TId, TRow> in(
      String fieldName, Collection<TSource> sourceCollection, Function<TSource, ?> valueExtractor) {
    List<?> values = sourceCollection.stream().map(valueExtractor).toList();
    return in(fieldName, values);
  }

  public Query<TId, TRow> in(String fieldName, Collection<?> valuesPassed) {
    Collection<?> values = valuesPassed == null ? Set.of() : valuesPassed;
    if (values.size() == 1) {
      add(fieldName, new Equals(values.iterator().next()));
    } else {
      add(fieldName, new In(values));
      if (values.isEmpty()) {
        guaranteedToYieldEmptyResultset = true;
      }
    }

    return this;
  }

  public Query<TId, TRow> inArr(String fieldName, Object... values) {
    return in(fieldName, Arrays.asList(values));
  }

  public Query<TId, TRow> notInArr(String fieldName, Object... values) {
    return notIn(fieldName, Arrays.asList(values));
  }

  public <TSource> Query<TId, TRow> notIn(
      String fieldName, Collection<TSource> sourceCollection, Function<TSource, ?> valueExtractor) {
    List<?> values =
        sourceCollection == null
            ? List.of()
            : sourceCollection.stream().map(valueExtractor).toList();
    return notIn(fieldName, values);
  }

  public Query<TId, TRow> notIn(String fieldName, Collection<?> values) {
    if (CollectionUtils.isEmpty(values)) {
      // NOTE: When we have notIn clause with the empty collection, it effectively means that
      // filtering is NOT applied to that field
      return this;
    }

    if (values.size() == 1) {
      add(fieldName, new Equals(values.iterator().next()).not());
    } else {
      add(fieldName, new In(values).not());
    }
    return this;
  }

  public <A extends Comparable<A>> Query<TId, TRow> between(
      String fieldName, A lowerBoundary, A upperBoundary) {
    if (lowerBoundary.compareTo(upperBoundary) == 0) {
      add(fieldName, new Equals(lowerBoundary));
    } else {
      add(fieldName, new Between(lowerBoundary, upperBoundary));
    }
    return this;
  }

  public <A extends Comparable<A>> Query<TId, TRow> notBetween(
      String fieldName, A lowerBoundary, A upperBoundary) {
    if (lowerBoundary.compareTo(upperBoundary) == 0) {
      add(fieldName, new Equals(lowerBoundary).not());
    } else {
      add(fieldName, new Between(lowerBoundary, upperBoundary).not());
    }
    return this;
  }

  public Query<TId, TRow> stringLengthBetween(
      String fieldName, int lowerBoundary, int upperBoundary) {
    add(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary));
    return this;
  }

  public Query<TId, TRow> stringLengthNotBetween(
      String fieldName, int lowerBoundary, int upperBoundary) {
    add(fieldName, new StringLengthBetween(lowerBoundary, upperBoundary).not());
    return this;
  }

  public Query<TId, TRow> like(String fieldName, String likeExpression) {
    add(fieldName, new Like(likeExpression));
    return this;
  }

  public Query<TId, TRow> notLike(String fieldName, String likeExpression) {
    add(fieldName, new Like(likeExpression).not());
    return this;
  }

  public Query<TId, TRow> contains(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, true));
    return this;
  }

  public Query<TId, TRow> notContains(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, true).not());
    return this;
  }

  public Query<TId, TRow> startsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, false, true));
    return this;
  }

  public Query<TId, TRow> notStartsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, false, true).not());
    return this;
  }

  public Query<TId, TRow> endsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, false));
    return this;
  }

  public Query<TId, TRow> notEndsWith(String fieldName, String subString) {
    add(fieldName, new Like(subString, true, false).not());
    return this;
  }

  public Query<TId, TRow> empty(String fieldName) {
    add(fieldName, new Empty());
    return this;
  }

  public Query<TId, TRow> notEmpty(String fieldName) {
    add(fieldName, new Empty().not());
    return this;
  }

  public Query<TId, TRow> lengthLe(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, true));
    return this;
  }

  public Query<TId, TRow> lengthLess(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, false));
    return this;
  }

  public Query<TId, TRow> lengthGe(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, false).not());
    return this;
  }

  public Query<TId, TRow> lengthGreater(String fieldName, int value) {
    add(fieldName, new StringLengthLess(value, true).not());
    return this;
  }

  /**
   * A convenient shortcut for creating new {@link JoinQuery} with this query being primary by
   * default.
   *
   * <p>Once obtained, add joins as needed. Afterward invoke one of the select methods to switch to
   * selection mode and finally call methods to retrieve data.
   *
   * @return join query API
   */
  public JoinQuery<TId, TRow> toJoin() {
    return service.buildJoinQuery(this);
  }

  /**
   * @return OrderByBuilder instance for ordering by columns, represented by this query. You must
   *     use this way of constructing {@link OrderBy} when sorting data retrieved by {@link
   *     JoinQuery}. In all other cases the intended use case is to get orderBy using {@link
   *     EasyCrudService}
   */
  public OrderByBuilder<TRow> orderBy(Function<TRow, ?> fieldNameGetter) {
    return new OrderByBuilder<>(service.getNameResolver(), fieldNameGetter, this);
  }

  public EasyCrudService<TId, TRow> getService() {
    return service;
  }

  public String getAlias() {
    return alias;
  }

  /**
   * Do not set alias after constructing a query yourself. This method is for internal library
   * usage.
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Query<?, ?> query = (Query<?, ?>) o;
    return Objects.equals(service.getRowMessageCode(), query.service.getRowMessageCode())
        && Objects.equals(alias, query.alias)
        && Objects.equals(conditions, query.conditions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(service.getRowMessageCode(), alias, conditions);
  }
}
