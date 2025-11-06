package org.summerb.easycrud.join_query.impl;

import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.dao.EasyCrudDaoSqlImpl;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;

@SuppressWarnings("unchecked")
public class QuerySpecificsResolverImpl implements QuerySpecificsResolver {
  @Override
  public String getTableName(Query<?, ?> query) {
    return ((EasyCrudServiceImpl<?, ?, EasyCrudDaoSqlImpl<?, ?>>) query.getService())
        .getDao()
        .getTableName();
  }

  @Override
  public <TRow extends HasId<TId>, TId> Class<TRow> getRowClass(Query<TId, TRow> query) {
    return query.getService().getRowClass();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <TRow extends HasId<TId>, TId> RowMapper<TRow> getRowMapper(Query<TId, TRow> query) {
    return (RowMapper<TRow>)
        ((EasyCrudServiceImpl<?, ?, EasyCrudDaoSqlImpl<?, ?>>) query.getService())
            .getDao()
            .getRowMapper();
  }

  @Override
  public <TRow extends HasId<TId>, TId> EasyCrudWireTap<TRow> getWireTap(Query<TId, TRow> query) {
    return (EasyCrudWireTap<TRow>)
        ((EasyCrudServiceImpl<?, ?, EasyCrudDaoSqlImpl<?, ?>>) query.getService()).getWireTap();
  }

  @Override
  public <TId, TRow extends HasId<TId>> EasyCrudExceptionStrategy<TId, TRow> getExceptionStrategy(
      Query<TId, TRow> query) {
    return (EasyCrudExceptionStrategy<TId, TRow>)
        ((EasyCrudServiceImpl<?, ?, EasyCrudDaoSqlImpl<?, ?>>) query.getService())
            .getExceptionStrategy();
  }

  @Override
  public <TId, TRow extends HasId<TId>> String getRowMessageCode(Query<TId, TRow> query) {
    return query.getService().getRowMessageCode();
  }
}
