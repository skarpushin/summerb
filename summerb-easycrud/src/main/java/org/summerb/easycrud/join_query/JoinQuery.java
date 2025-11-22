package org.summerb.easycrud.join_query;

import java.util.List;
import java.util.function.Function;
import org.summerb.easycrud.join_query.impl.JoinQueryElement;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

/**
 * Builds SQL JOIN queries by combining multiple {@link Query} instances (each representing a query
 * to one database table) into a single statement. Designed for simple use cases where you need to:
 * - Fetch related data across multiple tables - Apply filtering and sorting using fields from
 * multiple tables.
 *
 * @param <TId> Primary table's ID type
 * @param <TRow> Primary table's row type, must implement {@link HasId}
 */
public interface JoinQuery<TId, TRow extends HasId<TId>> {

  /**
   * Adds an INNER JOIN that matches primary table (as denoted by the {@link #getPrimaryQuery()}) FK
   * to the joined table PK.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN addedQuery.TableName ON primaryQuery.TableName.fk = addedQuery.TableName.id</pre>
   *
   * @param <TAddedId> PK type of the joined table
   * @param <TAddedRow> Row class of the joined table
   * @param addedQuery Query representing the table to join and optional conditions to filter on
   *     that table
   * @param idOfAddedTableGetter Lambda that will be used to extract the field name from the primary
   *     table that references to the joined table primary key
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> join(
      Query<TAddedId, TAddedRow> addedQuery, Function<TRow, TAddedId> idOfAddedTableGetter);

  /**
   * Adds an INNER JOIN between a table that is already part of this JoinQuery (an existing one) and
   * another table (added).
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN addedQuery.TableName ON existingQuery.TableName.fk = addedQuery.TableName.id</pre>
   *
   * @param existingQuery a query that denotes ONE table
   * @param addedQuery a query that denotes ADDED table
   * @param idOfAddedTableGetter Lambda that will be used to extract the field name from the ONE
   *     table that references to the ADDED table primary key
   * @return self
   */
  <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> join(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TExistingRow, TAddedId> idOfAddedTableGetter);

  /**
   * Adds an INNER JOIN where the target table foreign key references PK on the primary table.
   *
   * <p>WARNING: this might lead to a cartesian product if you're adding a table for one-to-many
   * reference, meaning rows will be duplicated. In such case consider turning on deduplication mode
   * via invoking {@link #deduplicate()} method. Also consider using {@link #exists(Query,
   * Function)} instead.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN addedQuery.TableName ON addedQuery.TableName.fk = primaryQuery.TableName.id</pre>
   *
   * @param <TAddedId> Target table's ID type
   * @param <TAddedRow> Target table's row type
   * @param addedQuery Query representing the table to join
   * @param idOfPrimaryTableGetter Function extracting the primary key from target table rows
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> joinBack(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter);

  /**
   * Adds an INNER JOIN between a table that is already part of this JoinQuery (existing) and
   * another table (added).
   *
   * <p>WARNING: this might lead to a cartesian product if you're adding a table for one-to-many
   * reference, meaning rows will be duplicated. In such a case consider turning on deduplication
   * mode via invoking {@link #deduplicate()} method. Also consider using {@link #exists(Query,
   * Function)} instead.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>JOIN addedQuery.TableName ON addedQuery.TableName.fk = existingQuery.TableName.id</pre>
   *
   * @param existingQuery a query that denotes EXISTING table
   * @param addedQuery a query that denotes ADDED table
   * @param idOfExistingTableGetter Lambda that will be used to extract the field name from the
   *     ADDED table that references the EXISTING table primary key
   * @return self
   */
  <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> joinBack(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, TExistingId> idOfExistingTableGetter);

  /**
   * Adds a LEFT JOIN to match primary table FK to joined table PK.
   *
   * <p>If addedQuery contain any filtering conditions, they will be added to the join clause. If
   * you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of the left join
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN addedQuery.TableName ON primaryQuery.TableName.fk = addedQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <TAddedId> Target table's ID type
   * @param <TAddedRow> Target table's row type
   * @param addedQuery Query representing the table to join
   * @param idOfAddedTableGetter Function extracting the foreign key from primary table rows
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> leftJoin(
      Query<TAddedId, TAddedRow> addedQuery, Function<TRow, TAddedId> idOfAddedTableGetter);

  /**
   * Adds a LEFT JOIN between a table that is already mentioned (existing) in this JoinQuery and
   * another (added) table.
   *
   * <p>If the new query contains any filtering conditions, they will be added to the JOIN clause.
   * If you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of this left join.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN addedQuery.TableName ON existingQuery.TableName.fk = addedQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param existingQuery a query that denotes ONE table
   * @param addedQuery a query that denotes OTHER table
   * @param idOfAddedTableGetter Lambda that will be used to extract the field name from the ONE
   *     table that references to the OTHER table primary key
   * @return self
   */
  <
          TExistingId,
          TExistingRow extends HasId<TExistingId>,
          TAddedId,
          TAddedRow extends HasId<TAddedId>>
      JoinQuery<TId, TRow> leftJoin(
          Query<TExistingId, TExistingRow> existingQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TExistingRow, TAddedId> idOfAddedTableGetter);

  /**
   * Adds a LEFT JOIN to match joined table FK to primary table PK.
   *
   * <p>If addedQuery contain any filtering conditions, they will be added to the join clause. If
   * you want conditions to be added to the WHERE clause, use regular join (which is inner join)
   * instead of the left join
   *
   * <p>WARNING: this might lead to a cartesian product if you're adding a table for one-to-many
   * reference, meaning rows will be duplicated. In such case consider turning on deduplication mode
   * via invoking {@link #deduplicate()} method. Also consider using {@link #exists(Query,
   * Function)} instead.
   *
   * <p>Think of calling this method as if adding the following JOIN clause (pseudocode):
   *
   * <pre>
   * LEFT JOIN addedQuery.TableName ON primaryQuery.TableName.fk = addedQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <TAddedId> Target table's ID type
   * @param <TAddedRow> Target table's row type
   * @param addedQuery Query representing the table to join
   * @param idOfPrimaryTableGetter Function extracting the foreign key from the added table that
   *     points to primary table
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> leftJoinBack(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter);

  /**
   * Adds NOT EXISTS to the WHERE clause to make sure there are no records exist in the added table
   * that refers to the primary table.
   *
   * <p>NOTE: At this time it is possible to add only 1 table to this check (meaning no joins can be
   * added into the EXISTS clause itself)
   *
   * <p>NOTE 2: Although this is not a JOIN "per se", this is often called "anti-join" -- operation
   * opposite to performing inner join, therefore, it is added to this API.
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND NOT EXISTS (SELECT 1 FROM addedQuery.TableName ON addedQuery.TableName.fk = primaryQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <TAddedId> Added table ID type
   * @param <TAddedRow> Added table row type
   * @param addedQuery Query representing the table to join
   * @param idOfPrimaryTableGetter Function extracting the foreign key from the added table which
   *     points to primary table PK
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> notExists(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter);

  /**
   * Adds NOT EXISTS to the WHERE clause to make sure there are no records exist in the ADDED table
   * that refers to a table that was already referenced in the previously added JOIN query.
   *
   * <p>NOTE: Although this is not a JOIN "per se", this is often called "anti-join" -- operation
   * opposite to performing inner join, therefore, it is added to this API.
   *
   * <p>NOTE 2: At this time it is possible to add only 1 table to this check (meaning no joins can
   * be added into the EXISTS clause itself)
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND NOT EXISTS (SELECT 1 FROM addedQuery.TableName ON addedQuery.TableName.fk = existingJoinQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param existingJoinQuery a query that denotes OTHER table
   * @param addedQuery a query that denotes ADDED table
   * @param idOfSecondaryTableGetter Lambda that will be used to extract the field name from the
   *     ADDED table that references to the OTHER table primary key
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>, ExistingId, ExistingRow extends HasId<ExistingId>>
      JoinQuery<TId, TRow> notExists(
          Query<ExistingId, ExistingRow> existingJoinQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, ExistingId> idOfSecondaryTableGetter);

  /**
   * Adds EXISTS to the WHERE clause to filter by data in the added table that refers to the primary
   * table. In a sense, this is an alternative to {@link #leftJoinBack(Query, Function)} which
   * allows to perform filtering but avoid cartesian products.
   *
   * <p>NOTE: At this time it is possible to add only 1 table to this check (meaning no joins can be
   * added into the EXISTS clause itself)
   *
   * <p>NOTE 2: Although this is not a JOIN "per se", this seems to be a viable alternative to
   * joining that other table, which in the case of one-to-many relationship, will result in
   * cartesian product. Therefore, it is added to this API.
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND EXISTS (SELECT 1 FROM addedQuery.TableName ON addedQuery.TableName.fk = primaryQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param <TAddedId> Added table ID type
   * @param <TAddedRow> Added table row type
   * @param addedQuery Query representing the table to join
   * @param idOfPrimaryTableGetter Function extracting the foreign key from the added table which
   *     points to primary table PK
   * @return self
   */
  <TAddedId, TAddedRow extends HasId<TAddedId>> JoinQuery<TId, TRow> exists(
      Query<TAddedId, TAddedRow> addedQuery, Function<TAddedRow, TId> idOfPrimaryTableGetter);

  /**
   * Adds EXISTS to the WHERE clause to filter by data in the added table that refers to some table
   * that was already referenced in the previously added JOIN query. In a sense, this is an
   * alternative to {@link #leftJoinBack(Query, Function)} which allows to perform filtering but
   * avoid cartesian products.
   *
   * <p>NOTE: At this time it is possible to add only 1 table to this check (meaning no joins can be
   * added into the EXISTS clause itself)
   *
   * <p>NOTE 2: Although this is not a JOIN "per se", this seems to be a viable alternative to
   * joining that other table, which in the case of one-to-many relationship, will result in
   * cartesian product. Therefore, it is added to this API.
   *
   * <p>Think of calling this method as if adding the following statement to the WHERE clause
   * (pseudocode):
   *
   * <pre>
   * WHERE ... AND EXISTS (SELECT 1 FROM addedQuery.TableName ON addedQuery.TableName.fk = existingJoinQuery.TableName.id [AND addedQuery.TableName.field1 = :value1 [AND ...]]
   * </pre>
   *
   * @param existingJoinQuery a query that denotes OTHER table
   * @param addedQuery a query that denotes ADDED table
   * @param idOfSecondaryTableGetter Lambda that will be used to extract the field name from the
   *     ADDED table that references to the OTHER table primary key
   * @return self
   */
  <
          TAddedId,
          TAddedRow extends HasId<TAddedId>,
          TExistingId,
          TExistingRow extends HasId<TExistingId>>
      JoinQuery<TId, TRow> exists(
          Query<TExistingId, TExistingRow> existingJoinQuery,
          Query<TAddedId, TAddedRow> addedQuery,
          Function<TAddedRow, TExistingId> idOfSecondaryTableGetter);

  /**
   * When invoked, results will be deduplicated using a window function using ROW_NUMBER() over
   * partition by ID of the table denoted by the ({@link #getPrimaryQuery()}) and ordering by
   * backward-joined tables IDs (including the other ordering on those tables, if any)
   *
   * @return self
   */
  JoinQuery<TId, TRow> deduplicate();

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
   * @return The primary query whose table appears in the FROM clause
   */
  Query<TId, TRow> getPrimaryQuery();

  /**
   * @return All configured JOIN elements in this query
   */
  List<JoinQueryElement> getJoins();

  /**
   * @return All configured EXISTS and NOT EXISTS elements in this query
   */
  List<JoinQueryElement> getExistenceConditions();

  /**
   * @return All participating queries (primary and joined tables) in this JOIN query
   */
  List<Query<?, ?>> getQueries();

  /**
   * @return true if deduplication of data from table denoted by primary query is requested
   */
  boolean isDeduplicate();
}
