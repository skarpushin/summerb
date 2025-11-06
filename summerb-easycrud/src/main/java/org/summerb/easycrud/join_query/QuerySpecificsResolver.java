package org.summerb.easycrud.join_query;

import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;

public interface QuerySpecificsResolver {
  String getTableName(Query<?, ?> query);

  <TRow extends HasId<TId>, TId> Class<TRow> getRowClass(Query<TId, TRow> query);

  <TRow extends HasId<TId>, TId> RowMapper<TRow> getRowMapper(Query<TId, TRow> query);

  <TRow extends HasId<TId>, TId> EasyCrudWireTap<TRow> getWireTap(Query<TId, TRow> query);

  <TId, TRow extends HasId<TId>> EasyCrudExceptionStrategy<TId, TRow> getExceptionStrategy(
      Query<TId, TRow> query);

  <TId, TRow extends HasId<TId>> String getRowMessageCode(Query<TId, TRow> query);
}
