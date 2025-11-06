package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinedSelect;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.Select;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.SqlBuilder;

public class SelectFactoryImpl implements SelectFactory {
  protected QuerySpecificsResolver querySpecificsResolver;
  protected NamedParameterJdbcTemplateEx jdbc;
  protected SqlBuilder sqlBuilder;

  public SelectFactoryImpl(
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      NamedParameterJdbcTemplateEx jdbc) {
    Preconditions.checkNotNull(jdbc, "jdbc is required");
    Preconditions.checkNotNull(querySpecificsResolver, "querySpecificsResolver is required");
    Preconditions.checkNotNull(sqlBuilder, "sqlBuilder is required");

    this.jdbc = jdbc;
    this.querySpecificsResolver = querySpecificsResolver;
    this.sqlBuilder = sqlBuilder;
  }

  @Override
  public <TRow extends HasId<TId>, TId> Select<TId, TRow> build(
      JoinQuery<?, ?> joinQuery, Query<TId, TRow> entityToSelect) {
    return new SelectImpl<>(joinQuery, entityToSelect, jdbc, querySpecificsResolver, sqlBuilder);
  }

  @Override
  public JoinedSelect build(JoinQuery<?, ?> joinQuery, List<Query<?, ?>> entitiesToSelect) {
    return new JoinedSelectImpl(
        joinQuery, entitiesToSelect, jdbc, querySpecificsResolver, sqlBuilder);
  }
}
