package org.summerb.easycrud.query;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.row.HasId;

/**
 * Condition for OR between several queries.
 *
 * @param <TId> type of row ID
 * @param <TRow> type of row
 */
public class DisjunctionCondition<TId extends Comparable<TId>, TRow extends HasId<TId>>
    extends Condition {

  /** List of queries in disjunction */
  protected List<Query<TId, TRow>> queries;

  /**
   * Constructor for DisjunctionCondition.
   *
   * @param disjunctions list of queries
   */
  public DisjunctionCondition(List<Query<TId, TRow>> disjunctions) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(disjunctions), "non-empty queries required");
    this.queries = disjunctions;
  }

  /**
   * @return list of queries in disjunction
   */
  public List<Query<TId, TRow>> getQueries() {
    return queries;
  }

  /**
   * @param queries list of queries in disjunction
   */
  public void setQueries(List<Query<TId, TRow>> queries) {
    this.queries = queries;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    DisjunctionCondition<?, ?> that = (DisjunctionCondition<?, ?>) o;
    return Objects.equals(queries, that.queries);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(queries);
  }
}
