package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.join_query.ConditionsLocation;
import org.summerb.easycrud.join_query.JoinDirection;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinedSelect;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.Select;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.join_query.model.JoinType;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.FieldsEnlister;

/**
 * This class represents a simplistic join query: it allows joining several tables, apply filtering,
 * sorting and pagination parameters. It also allows loading several entities at once - works
 * especially well when there is a 1:1 relationship in query results.
 *
 * <p>While this will be useful in some cases, it is not sophisticated enough to cover all use
 * cases. So feel free to switch to {@link
 * org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate} whenever you need more
 * sophisticated querying
 *
 * <p>Once you called all neccessary join methods, proceed with either calling #select() for
 * selecting only primary rows OR #select(Query ...selections) to select from multiple tables at
 * once (each row from the result set will contain data for joined row types, and will be
 * deserialized respectively)
 *
 * @param <TRow> type of the primary row class that is being selected
 */
public class JoinQueryImpl<TId, TRow extends HasId<TId>> implements JoinQuery<TId, TRow> {
  protected static final Logger log = LoggerFactory.getLogger(JoinQueryImpl.class);
  protected Set<JoinType> JOIN_TYPES = Set.of(JoinType.INNER, JoinType.LEFT);

  protected SelectFactory selectFactory;
  protected QuerySpecificsResolver querySpecificsResolver;
  protected FieldsEnlister fieldsEnlister;

  /** What is being queried - primary selection */
  protected Query<TId, TRow> primarySelection;

  /** All queries which participate in this query */
  protected List<Query<?, ?>> queries = new ArrayList<>();

  protected List<Query<?, ?>> queriesUnmodifiable;

  /** Conditions locations */
  protected Map<Query<?, ?>, ConditionsLocation> mapQueryToConditionLocation =
      new IdentityHashMap<>();

  /** Join directions */
  protected Map<Query<?, ?>, JoinDirection> mapQueryToJoinDirection = new IdentityHashMap<>();

  /** Joins and their conditions */
  protected List<JoinQueryElement> joins = new LinkedList<>();

  protected List<JoinQueryElement> joinsUnmodifiable;

  /** Elements for EXISTS and NOT EXISTS clauses. */
  protected List<JoinQueryElement> existence = new LinkedList<>();

  protected List<JoinQueryElement> existenceUnmodifiable;

  /** if true, selection should deduplicate results */
  protected boolean deduplicate;

  public JoinQueryImpl(
      Query<TId, TRow> primarySelection,
      SelectFactory selectFactory,
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister) {
    Preconditions.checkArgument(primarySelection != null, "primarySelection must be provided");
    Preconditions.checkArgument(selectFactory != null, "selectFactory must be provided");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver must be provided");
    Preconditions.checkArgument(fieldsEnlister != null, "fieldsEnlister must be provided");

    this.fieldsEnlister = fieldsEnlister;
    this.querySpecificsResolver = querySpecificsResolver;
    this.selectFactory = selectFactory;
    this.primarySelection = primarySelection;

    addQuery(primarySelection, ConditionsLocation.WHERE, JoinDirection.FORWARD);
  }

  @Override
  public List<JoinQueryElement> getJoins() {
    if (joinsUnmodifiable == null) {
      joinsUnmodifiable = Collections.unmodifiableList(joins);
    }
    return joinsUnmodifiable;
  }

  @Override
  public List<JoinQueryElement> getExistenceConditions() {
    if (existenceUnmodifiable == null) {
      existenceUnmodifiable = Collections.unmodifiableList(existence);
    }
    return existenceUnmodifiable;
  }

  @Override
  public List<Query<?, ?>> getQueries() {
    if (queriesUnmodifiable == null) {
      queriesUnmodifiable = Collections.unmodifiableList(queries);
    }
    return queriesUnmodifiable;
  }

  @Override
  public JoinQuery<TId, TRow> deduplicate() {
    deduplicate = true;
    return this;
  }

  @Override
  public boolean isDeduplicate() {
    if (!deduplicate) {
      return false;
    }

    boolean hasAtLeastOneBackwardJoin =
        getQueries().stream().anyMatch(x -> getJoinDirection(x) == JoinDirection.BACKWARD);
    if (!hasAtLeastOneBackwardJoin) {
      log.warn(
          "JoinQuery deduplication requested, but no backward joins (one-to-many relationships which might produce cartesian products) found");
    }

    return hasAtLeastOneBackwardJoin;
  }

  @Override
  public Query<TId, TRow> getPrimaryQuery() {
    return primarySelection;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> join(
      Query<TAddedId, TAddedRow> addedQuery, Function<TRow, TAddedId> idOfAddedTableGetter) {

    addQuery(addedQuery, ConditionsLocation.WHERE, JoinDirection.FORWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER,
            primarySelection,
            primarySelection.name(idOfAddedTableGetter),
            addedQuery));

    return this;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> joinBack(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter) {

    addQuery(addedQuery, ConditionsLocation.WHERE, JoinDirection.BACKWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, addedQuery, addedQuery.name(idOfPrimaryTableGetter), primarySelection));

    return this;
  }

  @Override
  public <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> joinBack(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, TExistingId> idOfExistingTableGetter) {

    Preconditions.checkArgument(
        isJoined(existingQuery), "existingQuery must already be a part of JOIN sequence");

    addQuery(addedQuery, ConditionsLocation.JOIN, JoinDirection.FORWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, addedQuery, addedQuery.name(idOfExistingTableGetter), existingQuery));

    return this;
  }

  @Override
  public <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> join(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TExistingRow, TAddedId> idOfAddedTableGetter) {

    Preconditions.checkArgument(
        isJoined(existingQuery), "existingQuery must already be a part of JOIN sequence");

    addQuery(addedQuery, ConditionsLocation.JOIN, JoinDirection.FORWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, existingQuery, existingQuery.name(idOfAddedTableGetter), addedQuery));

    return this;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> leftJoin(
      Query<TAddedId, TAddedRow> addedQuery, Function<TRow, TAddedId> idOfAddedTableGetter) {

    addQuery(addedQuery, ConditionsLocation.JOIN, JoinDirection.FORWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT,
            primarySelection,
            primarySelection.name(idOfAddedTableGetter),
            addedQuery));

    return this;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> leftJoinBack(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter) {

    addQuery(addedQuery, ConditionsLocation.JOIN, JoinDirection.BACKWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, addedQuery, addedQuery.name(idOfPrimaryTableGetter), primarySelection));

    return this;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> notExists(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter) {

    existence.add(
        new JoinQueryElement(
            JoinType.NOT_EXISTS,
            addedQuery,
            addedQuery.name(idOfPrimaryTableGetter),
            primarySelection));

    return this;
  }

  @Override
  public <
          TAddedId,
          TAddedRow extends HasId<TAddedId>,
          ExistingId,
          ExistingRow extends HasId<ExistingId>>
      JoinQuery<TId, TRow> notExists(
          Query<ExistingId, ExistingRow> existingJoinQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, ExistingId> idOfSecondaryTableGetter) {

    Preconditions.checkArgument(
        isJoined(existingJoinQuery), "existingJoinQuery must already be a part of JOIN sequence");

    existence.add(
        new JoinQueryElement(
            JoinType.NOT_EXISTS,
            addedQuery,
            addedQuery.name(idOfSecondaryTableGetter),
            existingJoinQuery));

    return this;
  }

  @Override
  public <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> exists(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter) {

    existence.add(
        new JoinQueryElement(
            JoinType.EXISTS,
            addedQuery,
            addedQuery.name(idOfPrimaryTableGetter),
            primarySelection));

    return this;
  }

  @Override
  public <
          TAddedId,
          TAddedRow extends HasId<TAddedId>,
          TExistingId,
          TExistingRow extends HasId<TExistingId>>
      JoinQuery<TId, TRow> exists(
          Query<TExistingId, TExistingRow> existingJoinQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, TExistingId> idOfSecondaryTableGetter) {

    Preconditions.checkArgument(
        isJoined(existingJoinQuery), "existingJoinQuery must already be a part of JOIN sequence");

    existence.add(
        new JoinQueryElement(
            JoinType.EXISTS,
            addedQuery,
            addedQuery.name(idOfSecondaryTableGetter),
            existingJoinQuery));

    return this;
  }

  protected boolean isJoined(Query<?, ?> query) {
    return joins.stream()
        .anyMatch(x -> x.getReferer().equals(query) || x.getReferred().equals(query));
  }

  @Override
  public <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TExistingRow, TAddedId> idOfAddedTableGetter) {

    Preconditions.checkArgument(
        isJoined(existingQuery), "existingQuery must already be a part of JOIN sequence");

    addQuery(addedQuery, ConditionsLocation.JOIN, JoinDirection.FORWARD);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, existingQuery, existingQuery.name(idOfAddedTableGetter), addedQuery));

    return this;
  }

  protected void addQuery(
      Query<?, ?> query, ConditionsLocation conditionsLocation, JoinDirection joinDirection) {
    if (queries.contains(query)) {
      return;
    }

    ensureAliasesForQueriesToSameTable(query);

    queries.add(query);
    mapQueryToConditionLocation.put(query, conditionsLocation);
    mapQueryToJoinDirection.put(query, joinDirection);
  }

  protected void ensureAliasesForQueriesToSameTable(Query<?, ?> query) {
    String tableName = querySpecificsResolver.getTableName(query);
    int idx = 0;
    for (Query<?, ?> existingQuery : queries) {
      if (!querySpecificsResolver.getTableName(existingQuery).equals(tableName)) {
        continue;
      }

      if (existingQuery.getAlias() == null || existingQuery.getAlias().equals(tableName)) {
        existingQuery.setAlias(tableName + idx);
      }

      idx++;
    }

    if (query.getAlias() == null) {
      query.setAlias(idx == 0 ? tableName : tableName + idx);
    }
  }

  @Override
  public Select<TId, TRow> select() {
    return selectFactory.build(this, primarySelection);
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>> Select<TOneId, TOneRow> select(
      Query<TOneId, TOneRow> query) {

    if (query.equals(primarySelection)) {
      return selectFactory.build(this, query);
    }

    for (JoinQueryElement join : joins) {
      if (join.getReferer().equals(query)) {
        return selectFactory.build(this, query);
      } else if (join.getReferred().equals(query)) {
        return selectFactory.build(this, query);
      }
    }

    throw new IllegalArgumentException("Query is not registered as join query: " + query);
  }

  @Override
  public JoinedSelect select(Query<?, ?> query1, Query<?, ?> query2, Query<?, ?>... otherQueries) {
    List<Query<?, ?>> queries = parametersToList(query1, query2, otherQueries);
    assertAllRegistered(queries);
    return selectFactory.build(this, queries);
  }

  @Override
  public JoinedSelect selectAll() {
    return selectFactory.build(this, queries);
  }

  @Override
  public OrderBy[] parseOrderBy(String semicolonSeparatedValues) {
    if (!StringUtils.hasText(semicolonSeparatedValues)) {
      return null;
    }

    return parseOrderBy(semicolonSeparatedValues.split(";"));
  }

  @Override
  public OrderBy[] parseOrderBy(String[] orderByArrStr) {
    if (orderByArrStr == null || orderByArrStr.length == 0) {
      return null;
    }

    OrderBy[] ret = new OrderBy[orderByArrStr.length];
    for (int i = 0; i < orderByArrStr.length; i++) {
      String orderByStr = orderByArrStr[i];
      OrderBy parsed = OrderBy.parse(orderByStr);
      ret[i] = buildOrderByForJoinQuery(parsed);
    }

    return ret;
  }

  @Override
  public ConditionsLocation getConditionsLocationForQuery(Query<?, ?> query) {
    return mapQueryToConditionLocation.get(query);
  }

  @Override
  public JoinDirection getJoinDirection(Query<?, ?> query) {
    return mapQueryToJoinDirection.get(query);
  }

  protected OrderBy buildOrderByForJoinQuery(OrderBy parsed) {
    int periodIndex = parsed.getFieldName().indexOf(".");
    OrderBy ret;

    if (periodIndex > 0) {
      String alias = parsed.getFieldName().substring(0, periodIndex);
      String fieldName = parsed.getFieldName().substring(periodIndex + 1);
      Query<?, ?> query = getQueryByAlias(alias);
      validateFieldName(fieldName, querySpecificsResolver.getRowClass(query));
      ret = new OrderBy(fieldName, parsed.getDirection(), query);
    } else {
      // we expect fieldName from primary Row class
      String fieldName = parsed.getFieldName().substring(periodIndex + 1);
      validateFieldName(fieldName, querySpecificsResolver.getRowClass(primarySelection));
      ret = new OrderBy(fieldName, parsed.getDirection(), primarySelection);
    }

    if (parsed.getNullsLast() != null) {
      ret.setNullsLast(parsed.getNullsLast());
    }

    if (parsed.getCollate() != null) {
      ret.setCollate(parsed.getCollate());
    }

    return ret;
  }

  protected void validateFieldName(String fieldName, Class<?> rowClass) {
    List<String> fields = fieldsEnlister.findInClass(rowClass);
    Preconditions.checkArgument(
        fields.contains(fieldName), "Field not found in row class: " + rowClass);
  }

  protected Query<?, ?> getQueryByAlias(String alias) {
    return queries.stream()
        .filter(q -> q.getAlias().equals(alias))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Can't find query with alias: " + alias));
  }

  protected void assertAllRegistered(List<Query<?, ?>> queries) {
    for (Query<?, ?> query : queries) {
      boolean found = false;
      for (JoinQueryElement join : joins) {
        if (join.getReferer().equals(query) || join.getReferred().equals(query)) {
          found = true;
          break;
        }
      }
      if (!found) {
        throw new IllegalArgumentException("Query is not registered as join query: " + query);
      }
    }
  }

  protected List<Query<?, ?>> parametersToList(
      Query<?, ?> query1, Query<?, ?> query2, Query<?, ?>[] otherQueries) {
    int count = 2 + (otherQueries == null ? 0 : otherQueries.length);
    List<Query<?, ?>> ret = new ArrayList<>(count);
    ret.add(query1);
    ret.add(query2);
    if (otherQueries != null) {
      Collections.addAll(ret, otherQueries);
    }
    return ret;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JoinQueryImpl<?, ?> joinQuery = (JoinQueryImpl<?, ?>) o;
    return Objects.equals(primarySelection, joinQuery.primarySelection)
        && Objects.equals(joins, joinQuery.joins);
  }

  @Override
  public int hashCode() {
    return Objects.hash(primarySelection, joins);
  }
}
