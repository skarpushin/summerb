package org.summerb.easycrud.join_query;

import java.util.List;
import java.util.function.Function;
import org.summerb.easycrud.join_query.impl.JoinQueryElement;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

/**
 * Builds SQL JOIN queries by combining multiple {@link Query} instances (each representing a query
 * to one database table) into a single statement. Designed for simple use cases where you need to:
 * - Fetch related data across multiple tables - Apply filtering and sorting using fields from
 * multiple tables.
 *
 * <p>Primarily intended for many-to-one relationships where resulting data set would not contain
 * duplicate rows from the primary table (as outlined by {@link #getPrimaryQuery()}).
 *
 * @param <TId> Primary table's ID type
 * @param <TRow> Primary table's row type, must implement {@link HasId}
 */
public interface JoinQuery<TId, TRow extends HasId<TId>> {
  /**
   * @return The primary query whose table appears in the FROM clause
   */
  Query<TId, TRow> getPrimaryQuery();

  /**
   * @return All configured JOIN elements in this query
   */
  List<JoinQueryElement> getJoins();

  /**
   * @return All participating queries (primary + joined tables) in this JOIN query
   */
  List<Query<?, ?>> getQueries();

  /**
   * Adds an INNER JOIN with automatic foreign key detection.
   *
   * <p>Automatically identifies the foreign key relationship by scanning for {@link ReferringTo}
   * annotations first on the primary table's fields, then on the target table's fields.
   *
   * @param otherQuery Query representing the table to join
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> otherQuery);

  /**
   * Adds a LEFT JOIN with automatic foreign key detection.
   *
   * <p>Automatically identifies the foreign key relationship by scanning for {@link ReferringTo}
   * annotations first on the primary table's fields, then on the target table's fields.
   *
   * @param otherQuery Query representing the table to join
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery);

  /**
   * Adds an INNER JOIN using explicit foreign key specification.
   *
   * @param otherQuery Query representing the table to join
   * @param otherIdGetter Function extracting the foreign key from primary table rows
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> otherQuery, Function<TRow, TOtherId> otherIdGetter);

  /**
   * Adds a LEFT JOIN using explicit foreign key specification.
   *
   * @param otherQuery Query representing the table to join
   * @param otherIdGetter Function extracting the foreign key from primary table rows
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery, Function<TRow, TOtherId> otherIdGetter);

  /**
   * Adds an INNER JOIN where the target table references the primary table.
   *
   * <p>Used when the joined table contains a foreign key pointing to the primary table.
   *
   * @param otherQuery Query representing the table to join
   * @param primaryIdGetter Function extracting the primary key from target table rows
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> joinBack(
      Query<TOtherId, TOtherRow> otherQuery, Function<TOtherRow, TId> primaryIdGetter);

  /**
   * Adds a LEFT JOIN where the target table references the primary table.
   *
   * <p>Used when the joined table contains a foreign key pointing to the primary table.
   *
   * @param otherQuery Query representing the table to join
   * @param primaryIdGetter Function extracting the primary key from target table rows
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoinBack(
      Query<TOtherId, TOtherRow> otherQuery, Function<TOtherRow, TId> primaryIdGetter);

  /**
   * Adds an INNER JOIN between two non-primary tables with explicit foreign key.
   *
   * <p>One of the specified tables must be connected to the primary table through existing joins.
   *
   * @param sourceQuery Source table query (most likely already joined to primary)
   * @param otherQuery Target table query to join
   * @param otherIdGetter Function extracting foreign key from source to target table
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> sourceQuery,
          Query<TOtherId, TOtherRow> otherQuery,
          Function<TOneRow, TOtherId> otherIdGetter);

  /**
   * Adds a LEFT JOIN between two non-primary tables with explicit foreign key.
   *
   * <p>One of the specified tables must be connected to the primary table through existing joins.
   *
   * @param sourceQuery Source table query (most likely already joined to primary)
   * @param otherQuery Target table query to join
   * @param otherIdGetter Function extracting foreign key from source to target table
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> sourceQuery,
          Query<TOtherId, TOtherRow> otherQuery,
          Function<TOneRow, TOtherId> otherIdGetter);

  /**
   * Adds an INNER JOIN between two non-primary tables with automatic foreign key detection.
   *
   * <p>Automatically identifies foreign key using {@link ReferringTo} annotations on either table.
   * Both tables must be connected to the primary table through existing joins.
   *
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> oneQuery, Query<TOtherId, TOtherRow> otherQuery);

  /**
   * Adds a LEFT JOIN between two non-primary tables with automatic foreign key detection.
   *
   * <p>Automatically identifies foreign key using {@link ReferringTo} annotations on either table.
   * Both tables must be connected to the primary table through existing joins.
   *
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> oneQuery, Query<TOtherId, TOtherRow> otherQuery);

  /**
   * Once you finished configuring this Join Query, call this method to create Selector for primary
   * table rows/columns only
   *
   * @return Selector API
   */
  Select<TId, TRow> select();

  /**
   * Once you finished configuring this Join Query, call this method to create Selector for specific
   * table rows
   *
   * @param query Target table query to select from
   * @return Selector API for the specified table's rows
   */
  <TOneId, TOneRow extends HasId<TOneId>> Select<TOneId, TOneRow> select(
      Query<TOneId, TOneRow> query);

  /**
   * Once you finished configuring this Join Query, call this method to create Selector for specific
   * table rows
   *
   * <p>Note: Retrieving data from multiple tables in one query may impact performance due to
   * increased data transfer and potential Cartesian products.
   *
   * @return Selector API for multiple table rows
   */
  JoinedSelect select(Query<?, ?> query1, Query<?, ?> query2, Query<?, ?>... otherQueries);

  /**
   * Once you finished configuring this Join Query, call this method to create Selector for getting
   * data from all mentioned tables
   *
   * <p>Note: Retrieving data from multiple tables in one query may impact performance due to
   * increased data transfer and potential Cartesian products.
   *
   * @return Selector API for all table rows
   */
  JoinedSelect selectAll();

  /**
   * Parse {@link OrderBy} array from semi-colon separated values, i.e.
   * "users.karma,asc;posts.title,asc;comments.comment,asc".
   */
  OrderBy[] parseOrderBy(String semicolonSeparatedValues);

  /**
   * Parse {@link OrderBy} array from individual order by statements ["users.karma,asc",
   * "posts.title,asc", "comments.comment,asc"]
   */
  OrderBy[] parseOrderBy(String[] orderByStr);
}
