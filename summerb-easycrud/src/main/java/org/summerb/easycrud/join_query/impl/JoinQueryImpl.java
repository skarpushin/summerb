package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.join_query.ConditionsLocation;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinedSelect;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;
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
  protected ReferringToFieldsFinder referringToFieldsFinder;
  protected SelectFactory selectFactory;
  protected QuerySpecificsResolver querySpecificsResolver;
  protected FieldsEnlister fieldsEnlister;

  /** What is being queried - primary selection */
  protected Query<TId, TRow> primarySelection;

  /** All queries which participate in this query */
  protected List<Query<?, ?>> queries = new ArrayList<>();

  protected List<Query<?, ?>> queriesUnmodifiable;

  /** Conditions locations */
  protected Map<Query<?, ?>, ConditionsLocation> mapQueryToConditionLocation = new HashMap<>();

  /** Joins and their conditions */
  protected List<JoinQueryElement> joins = new LinkedList<>();

  protected List<JoinQueryElement> joinsUnmodifiable;

  protected List<JoinQueryElement> notExists = new LinkedList<>();
  protected List<JoinQueryElement> notExistsUnmodifiable;

  public JoinQueryImpl(
      Query<TId, TRow> primarySelection,
      ReferringToFieldsFinder referringToFieldsFinder,
      SelectFactory selectFactory,
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister) {
    Preconditions.checkArgument(primarySelection != null, "primarySelection must be provided");
    Preconditions.checkArgument(
        referringToFieldsFinder != null, "referringToFieldsFinder must be provided");
    Preconditions.checkArgument(selectFactory != null, "selectFactory must be provided");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver must be provided");
    Preconditions.checkArgument(fieldsEnlister != null, "fieldsEnlister must be provided");

    this.fieldsEnlister = fieldsEnlister;
    this.querySpecificsResolver = querySpecificsResolver;
    this.selectFactory = selectFactory;
    this.primarySelection = primarySelection;
    this.referringToFieldsFinder = referringToFieldsFinder;

    addQuery(primarySelection, ConditionsLocation.WHERE);
  }

  @Override
  public List<JoinQueryElement> getJoins() {
    if (joinsUnmodifiable == null) {
      joinsUnmodifiable = Collections.unmodifiableList(joins);
    }
    return joinsUnmodifiable;
  }

  @Override
  public List<JoinQueryElement> getNotExists() {
    if (notExistsUnmodifiable == null) {
      notExistsUnmodifiable = Collections.unmodifiableList(notExists);
    }
    return notExistsUnmodifiable;
  }

  @Override
  public List<Query<?, ?>> getQueries() {
    if (queriesUnmodifiable == null) {
      queriesUnmodifiable = Collections.unmodifiableList(queries);
    }
    return queriesUnmodifiable;
  }

  @Override
  public Query<TId, TRow> getPrimaryQuery() {
    return primarySelection;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> otherQuery) {

    addQuery(otherQuery, ConditionsLocation.WHERE);

    return innerJoinByForeignKeyAutoDetect(primarySelection, otherQuery);
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> queryToJoin, Function<TRow, TOtherId> fkGetter) {

    addQuery(queryToJoin, ConditionsLocation.WHERE);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, primarySelection, primarySelection.name(fkGetter), queryToJoin));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> joinBack(
      Query<TOtherId, TOtherRow> queryToJoin, Function<TOtherRow, TId> fkGetter) {

    addQuery(queryToJoin, ConditionsLocation.WHERE);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, queryToJoin, queryToJoin.name(fkGetter), primarySelection));

    return this;
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> queryOne,
          Query<TOtherId, TOtherRow> queryOther,
          Function<TOneRow, TOtherId> queryOneFkGetter) {

    addQueries(queryOne, queryOther, ConditionsLocation.WHERE);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, queryOne, queryOne.name(queryOneFkGetter), queryOther));

    return this;
  }

  protected void addQueries(
      Query<?, ?> queryOne, Query<?, ?> queryOther, ConditionsLocation conditionsLocation) {
    Preconditions.checkArgument(
        (queries.contains(queryOne) != queries.contains(queryOther)),
        "Only (and exactly) one of the queries must have been already registered with this join query");

    addQuery(queryOne, conditionsLocation);
    addQuery(queryOther, conditionsLocation);
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> queryOther) {

    addQueries(queryOne, queryOther, ConditionsLocation.WHERE);

    return innerJoinByForeignKeyAutoDetect(queryOne, queryOther);
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> queryToJoin, Function<TRow, TOtherId> fkGetter) {

    addQuery(queryToJoin, ConditionsLocation.JOIN);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, primarySelection, primarySelection.name(fkGetter), queryToJoin));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoinBack(
      Query<TOtherId, TOtherRow> queryToJoin, Function<TOtherRow, TId> fkGetter) {

    addQuery(queryToJoin, ConditionsLocation.JOIN);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, queryToJoin, queryToJoin.name(fkGetter), primarySelection));

    return this;
  }

  @Override
  public <AddedTableIdType, AddedTableRowType extends HasId<AddedTableIdType>>
      JoinQuery<TId, TRow> notExists(
          Query<AddedTableIdType, AddedTableRowType> queryToAdd,
          Function<AddedTableRowType, TId> fkGetter) {

    notExists.add(
        new JoinQueryElement(
            JoinType.NOT_EXISTS, queryToAdd, queryToAdd.name(fkGetter), primarySelection));

    return this;
  }

  @Override
  public <
          AddedId,
          AddedRow extends HasId<AddedId>,
          ExistingId,
          ExistingRow extends HasId<ExistingId>>
      JoinQuery<TId, TRow> notExists(
          Query<AddedId, AddedRow> queryToAdd,
          Query<ExistingId, ExistingRow> existingJoinQuery,
          Function<AddedRow, ExistingId> fkGetter) {

    Preconditions.checkArgument(
        queries.contains(existingJoinQuery),
        "existingJoinQuery must be already registered within this join");
    Preconditions.checkArgument(
        mapQueryToConditionLocation.get(existingJoinQuery) != ConditionsLocation.NOT_EXISTS,
        "existingJoinQuery must not be used in other NOT EXIST clause");

    notExists.add(
        new JoinQueryElement(
            JoinType.NOT_EXISTS, queryToAdd, queryToAdd.name(fkGetter), existingJoinQuery));

    return this;
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> queryOne,
          Query<TOtherId, TOtherRow> queryOther,
          Function<TOneRow, TOtherId> queryOneFkGetter) {

    addQueries(queryOne, queryOther, ConditionsLocation.JOIN);

    joins.add(
        new JoinQueryElement(JoinType.LEFT, queryOne, queryOne.name(queryOneFkGetter), queryOther));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery) {

    addQuery(otherQuery, ConditionsLocation.JOIN);

    return leftJoinByForeignKeyAutoDetect(primarySelection, otherQuery);
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> queryOther) {

    addQueries(queryOne, queryOther, ConditionsLocation.JOIN);

    return leftJoinByForeignKeyAutoDetect(queryOne, queryOther);
  }

  protected void addQuery(Query<?, ?> query, ConditionsLocation conditionsLocation) {
    if (queries.contains(query)) {
      return;
    }

    ensureAliasesForQueriesToSameTable(query);

    queries.add(query);
    mapQueryToConditionLocation.put(query, conditionsLocation);
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

  protected <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> innerJoinByForeignKeyAutoDetect(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> otherQuery) {
    return joinByForeignKeyAutoDetect(queryOne, otherQuery, JoinType.INNER);
  }

  protected <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoinByForeignKeyAutoDetect(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> otherQuery) {
    return joinByForeignKeyAutoDetect(queryOne, otherQuery, JoinType.LEFT);
  }

  protected <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> joinByForeignKeyAutoDetect(
          Query<TOneId, TOneRow> queryOne,
          Query<TOtherId, TOtherRow> otherQuery,
          JoinType joinType) {
    String forwardLinkFieldName =
        referringToFieldsFinder.findReferringField(
            queryOne.getService().getRowClass(), otherQuery.getService().getRowClass());
    String backwardLinkFieldName =
        referringToFieldsFinder.findReferringField(
            otherQuery.getService().getRowClass(), queryOne.getService().getRowClass());

    if (forwardLinkFieldName != null && backwardLinkFieldName != null) {
      throw new RuntimeException(
          "Can't distinctively identify foreign key for joining "
              + queryOne.getService().getRowMessageCode()
              + " and "
              + otherQuery.getService().getRowMessageCode());
    }

    if (forwardLinkFieldName != null) {
      joins.add(new JoinQueryElement(joinType, queryOne, forwardLinkFieldName, otherQuery));
      return this;
    }

    if (backwardLinkFieldName != null) {
      joins.add(new JoinQueryElement(joinType, otherQuery, backwardLinkFieldName, queryOne));
      return this;
    }

    throw new RuntimeException(
        "Can't distinctively identify foreign key for joining "
            + queryOne.getService().getRowMessageCode()
            + " and "
            + otherQuery.getService().getRowMessageCode());
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
