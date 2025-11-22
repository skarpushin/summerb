package org.summerb.easycrud.join_query;

import java.util.List;
import java.util.function.Function;
import org.summerb.easycrud.join_query.impl.JoinQueryElement;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

// TODO: Add support for EXISTS clause so that we do not have to deal with cartesian products of
//  joined tables

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
   * @return All configured NOT EXISTS elements in this query
   */
  List<JoinQueryElement> getNotExists();

  /**
   * @return All participating queries (primary and joined tables) in this JOIN query
   */
  List<Query<?, ?>> getQueries();

  /**
   * When invoked, results will be deduplicated using a window function using ROW_NUMBER() over
   * partition by ID of the table denoted by the ({@link #getPrimaryQuery()}) and ordering by
   * backward-joined tables IDs (including the other ordering on those tables, if any)
   *
   * @return self
   */
  JoinQuery<TId, TRow> deduplicate();

  /**
   * @return true if deduplication of data from table denoted by primary query is requested
   */
  boolean isDeduplicate();

  /**
   * Adds an INNER JOIN using an explicit foreign key specification to match primary table FK to
   * joined table PK.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN queryToJoin.TableName ON primaryQuery.TableName.fk = queryToJoin.TableName.id</pre>
   *
   * @param <JoinedTableIdType> PK type of the joined table
   * @param <JoinedTableRowType> Row class of the joined table
   * @param queryToJoin Query representing the table to join and optional conditions to filter on
   *     that table
   * @param fkGetter Lambda that will be used to extract the field name from the primary table that
   *     references to the joined table primary key
   * @return self
   */
  <JoinedTableIdType, JoinedTableRowType extends HasId<JoinedTableIdType>>
      JoinQuery<TId, TRow> join(
          Query<JoinedTableIdType, JoinedTableRowType> queryToJoin,
          Function<TRow, JoinedTableIdType> fkGetter);

  /**
   * Adds an INNER JOIN between two non-primary tables using an explicit foreign key specification.
   * One of the specified tables is expected to already be in a join and transitively joined with
   * the primary table.
   *
   * @param queryOne a query that denotes ONE table
   * @param queryOther a query that denotes OTHER table
   * @param queryOneFkGetter Lambda that will be used to extract the field name from the ONE table
   *     that references to the OTHER table primary key
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> queryOne,
          Query<TOtherId, TOtherRow> queryOther,
          Function<TOneRow, TOtherId> queryOneFkGetter);

  /**
   * Adds an INNER JOIN where the target table foreign key references PK on the primary table.
   *
   * <p>WARNING: this might lead to a cartesian product if you're adding a table for one-to-many
   * reference, meaning rows will be duplicated. In such case consider turning on deduplication mode
   * via invoking {@link #deduplicate()} method.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN queryToJoin.TableName ON queryToJoin.TableName.fk = primaryQuery.TableName.id</pre>
   *
   * @param <JoinedTableIdType> Target table's ID type
   * @param <JoinedTableRowType> Target table's row type
   * @param queryToJoin Query representing the table to join
   * @param fkGetter Function extracting the primary key from target table rows
   * @return self
   */
  <JoinedTableIdType, JoinedTableRowType extends HasId<JoinedTableIdType>>
      JoinQuery<TId, TRow> joinBack(
          Query<JoinedTableIdType, JoinedTableRowType> queryToJoin,
          Function<JoinedTableRowType, TId> fkGetter);

  /**
   * Adds a LEFT JOIN using explicit foreign key specification to match primary table FK to joined
   * table PK.
   *
   * <p>If queryToJoin contain any filtering conditions, they will be added to the join clause. If
   * you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of the left join
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN queryToJoin.TableName ON primaryQuery.TableName.fk = queryToJoin.TableName.id [AND queryToJoin.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <JoinedTableIdType> Target table's ID type
   * @param <JoinedTableRowType> Target table's row type
   * @param queryToJoin Query representing the table to join
   * @param fkGetter Function extracting the foreign key from primary table rows
   * @return self
   */
  <JoinedTableIdType, JoinedTableRowType extends HasId<JoinedTableIdType>>
      JoinQuery<TId, TRow> leftJoin(
          Query<JoinedTableIdType, JoinedTableRowType> queryToJoin,
          Function<TRow, JoinedTableIdType> fkGetter);

  /**
   * Adds a LEFT JOIN between two non-primary tables using an explicit foreign key specification.
   * One of the specified tables is expected to already be in a join and transitively joined with
   * the primary table.
   *
   * <p>If the new query contains any filtering conditions, they will be added to the JOIN clause.
   * If you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of this left join.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN queryOne.TableName ON queryOne.TableName.fk = queryOther.TableName.id [AND new_query.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * NOTE: In the above example new_query represents the query that is being added with this call
   *
   * @param queryOne a query that denotes ONE table
   * @param queryOther a query that denotes OTHER table
   * @param queryOneFkGetter Lambda that will be used to extract the field name from the ONE table
   *     that references to the OTHER table primary key
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> queryOne,
          Query<TOtherId, TOtherRow> queryOther,
          Function<TOneRow, TOtherId> queryOneFkGetter);

  /**
   * Adds a LEFT JOIN using explicit foreign key specification to match joined table FK to primary
   * table PK.
   *
   * <p>If queryToJoin contain any filtering conditions, they will be added to the join clause. If
   * you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of the left join
   *
   * <p>WARNING: this might lead to a cartesian product if you're adding a table for one-to-many
   * reference, meaning rows will be duplicated. In such case consider turning on deduplication mode
   * via invoking {@link #deduplicate()} method.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN queryToJoin.TableName ON primaryQuery.TableName.fk = queryToJoin.TableName.id [AND queryToJoin.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <JoinedTableIdType> Target table's ID type
   * @param <JoinedTableRowType> Target table's row type
   * @param queryToJoin Query representing the table to join
   * @param fkGetter Function extracting the foreign key from primary table rows
   * @return self
   */
  <JoinedTableIdType, JoinedTableRowType extends HasId<JoinedTableIdType>>
      JoinQuery<TId, TRow> leftJoinBack(
          Query<JoinedTableIdType, JoinedTableRowType> queryToJoin,
          Function<JoinedTableRowType, TId> fkGetter);

  /**
   * Adds NOT EXISTS to the WHERE clause to make sure there are no records exist in the added table
   * that refers to the primary table.
   *
   * <p>NOTE: Although this is not a JOIN "per se", this is often called "anti-join" -- operation
   * opposite to performing inner join, therefore, it is added to this API.
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND NOT EXISTS (SELECT 1 FROM queryToAdd.TableName ON queryToAdd.TableName.fk = primaryQuery.TableName.id [AND queryToAdd.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <AddedTableIdType> Target table's ID type
   * @param <AddedTableRowType> Target table's row type
   * @param queryToAdd Query representing the table to join
   * @param fkGetter Function extracting the foreign key from the added table which points to
   *     primary table PK
   * @return self
   */
  <AddedTableIdType, AddedTableRowType extends HasId<AddedTableIdType>>
      JoinQuery<TId, TRow> notExists(
          Query<AddedTableIdType, AddedTableRowType> queryToAdd,
          Function<AddedTableRowType, TId> fkGetter);

  /**
   * Adds NOT EXISTS to the WHERE clause to make sure there are no records exist in the added table
   * that refers to some other table that was already referenced in the previously added JOIN query.
   *
   * <p>NOTE: Although this is not a JOIN "per se", this is often called "anti-join" -- operation
   * opposite to performing inner join, therefore, it is added to this API.
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND NOT EXISTS (SELECT 1 FROM queryToAdd.TableName ON queryToAdd.TableName.fk = existingJoinQuery.TableName.id [AND queryToAdd.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param queryToAdd a query that denotes ONE table
   * @param existingJoinQuery a query that denotes OTHER table
   * @param queryToAddFkGetter Lambda that will be used to extract the field name from the ONE table
   *     that references to the OTHER table primary key
   * @return self
   */
  <AddedId, AddedRow extends HasId<AddedId>, ExistingId, ExistingRow extends HasId<ExistingId>>
      JoinQuery<TId, TRow> notExists(
          Query<AddedId, AddedRow> queryToAdd,
          Query<ExistingId, ExistingRow> existingJoinQuery,
          Function<AddedRow, ExistingId> queryToAddFkGetter);

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

  /**
   * @return location of the conditions where filtering conditions of the given query will be
   *     located
   */
  ConditionsLocation getConditionsLocationForQuery(Query<?, ?> query);

  /**
   * @return direction of the join. {@link JoinDirection#FORWARD} means that this join represents
   *     many-to-one or one-to-one relation and cartesian product is not possible. {@link
   *     JoinDirection#BACKWARD} means otherwise
   */
  JoinDirection getJoinDirection(Query<?, ?> query);

  /**
   * Adds an INNER JOIN with automatic foreign key detection.
   *
   * <p>Automatically identifies the foreign key relationship by scanning for {@link ReferringTo}
   * annotations first on the primary table's fields, then on the target table's fields.
   *
   * <p>WARNING: Use with caution as this method introduces "magic" that cannot be verified at
   * compile time.
   *
   * @param otherQuery Query representing the table to join
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> join(
      Query<TOtherId, TOtherRow> otherQuery);

  /**
   * Adds an INNER JOIN between two non-primary tables with automatic foreign key detection.
   *
   * <p>Automatically identifies foreign key using {@link ReferringTo} annotations on either table.
   * Both tables must be connected to the primary table through existing joins.
   *
   * <p>WARNING: Use with caution as this method introduces "magic" that cannot be verified at
   * compile time.
   *
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> join(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> queryOther);

  /**
   * Adds a LEFT JOIN between two non-primary tables with automatic foreign key detection.
   *
   * <p>Automatically identifies foreign key using {@link ReferringTo} annotations on either table.
   * Both tables must be connected to the primary table through existing joins.
   *
   * <p>WARNING: Use with caution as this method introduces "magic" that cannot be verified at
   * compile time.
   *
   * @return self
   */
  <TOneId, TOneRow extends HasId<TOneId>, TOtherId, TOtherRow extends HasId<TOtherId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TOneId, TOneRow> queryOne, Query<TOtherId, TOtherRow> queryOther);

  /**
   * Adds a LEFT JOIN with automatic foreign key detection.
   *
   * <p>Automatically identifies the foreign key relationship by scanning for {@link ReferringTo}
   * annotations first on the primary table's fields, then on the target table's fields.
   *
   * <p>WARNING: Use with caution as this method introduces "magic" that cannot be verified at
   * compile time.
   *
   * @param otherQuery Query representing the table to join
   * @return self
   * @param <TOtherId> Target table's ID type
   * @param <TOtherRow> Target table's row type
   */
  <TOtherId, TOtherRow extends HasId<TOtherId>> JoinQuery<TId, TRow> leftJoin(
      Query<TOtherId, TOtherRow> otherQuery);
}
