package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.util.StringUtils;
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

  /** Joins and their conditions */
  protected List<JoinQueryElement> joins = new LinkedList<>();

  protected List<Query<?, ?>> queriesUnmodifiable;

  protected List<JoinQueryElement> joinsUnmodifiable;

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

    addQuery(primarySelection);
  }

  @Override
  public List<JoinQueryElement> getJoins() {
    if (joinsUnmodifiable == null) {
      joinsUnmodifiable = Collections.unmodifiableList(joins);
    }
    return joins;
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

    addQuery(otherQuery);

    return innerJoinByForeignKeyAutoDetect(primarySelection, otherQuery);
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery) {

    addQuery(otherQuery);

    return leftJoinByForeignKeyAutoDetect(primarySelection, otherQuery);
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> otherQuery, Function<TRow, TOtherId> otherIdGetter) {

    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, primarySelection, primarySelection.name(otherIdGetter), otherQuery));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery, Function<TRow, TOtherId> otherIdGetter) {

    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, primarySelection, primarySelection.name(otherIdGetter), otherQuery));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> joinBack(
      Query<TOtherId, TOtherRow> otherQuery, Function<TOtherRow, TId> primaryIdGetter) {

    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, otherQuery, otherQuery.name(primaryIdGetter), primarySelection));

    return this;
  }

  @Override
  public <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoinBack(
      Query<TOtherId, TOtherRow> otherQuery, Function<TOtherRow, TId> primaryIdGetter) {

    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, otherQuery, otherQuery.name(primaryIdGetter), primarySelection));

    return this;
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> sourceQuery,
          Query<TOtherId, TOtherRow> otherQuery,
          Function<TOneRow, TOtherId> otherIdGetter) {

    addQuery(sourceQuery);
    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.INNER, sourceQuery, sourceQuery.name(otherIdGetter), otherQuery));

    return this;
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> sourceQuery,
          Query<TOtherId, TOtherRow> otherQuery,
          Function<TOneRow, TOtherId> otherIdGetter) {

    addQuery(sourceQuery);
    addQuery(otherQuery);

    joins.add(
        new JoinQueryElement(
            JoinType.LEFT, sourceQuery, sourceQuery.name(otherIdGetter), otherQuery));

    return this;
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> oneQuery, Query<TOtherId, TOtherRow> otherQuery) {

    addQuery(oneQuery);
    addQuery(otherQuery);

    return innerJoinByForeignKeyAutoDetect(oneQuery, otherQuery);
  }

  @Override
  public <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> oneQuery, Query<TOtherId, TOtherRow> otherQuery) {

    addQuery(oneQuery);
    addQuery(otherQuery);

    return leftJoinByForeignKeyAutoDetect(oneQuery, otherQuery);
  }

  protected void addQuery(Query<?, ?> query) {
    if (queries.contains(query)) {
      return;
    }

    ensureAliasesForQueriesToSameTable(query);

    queries.add(query);
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
