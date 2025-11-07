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
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.SqlBuilder;

public class SelectFactoryImpl implements SelectFactory {
  protected QuerySpecificsResolver querySpecificsResolver;
  protected NamedParameterJdbcTemplateEx jdbc;
  protected SqlBuilder sqlBuilder;
  protected FieldsEnlister fieldsEnlister;

  public SelectFactoryImpl(
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      NamedParameterJdbcTemplateEx jdbc,
      FieldsEnlister fieldsEnlister) {
    Preconditions.checkArgument(jdbc != null, "jdbc is required");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver is required");
    Preconditions.checkArgument(sqlBuilder != null, "sqlBuilder is required");
    Preconditions.checkArgument(fieldsEnlister != null, "fieldsEnlister is required");

    this.jdbc = jdbc;
    this.querySpecificsResolver = querySpecificsResolver;
    this.sqlBuilder = sqlBuilder;
    this.fieldsEnlister = fieldsEnlister;
  }

  @Override
  public <TRow extends HasId<TId>, TId> Select<TId, TRow> build(
      JoinQuery<?, ?> joinQuery, Query<TId, TRow> entityToSelect) {
    return new SelectImpl<>(
        joinQuery, entityToSelect, jdbc, querySpecificsResolver, sqlBuilder, fieldsEnlister);
  }

  @Override
  public JoinedSelect build(JoinQuery<?, ?> joinQuery, List<Query<?, ?>> entitiesToSelect) {
    return new JoinedSelectImpl(
        joinQuery, entitiesToSelect, jdbc, querySpecificsResolver, sqlBuilder, fieldsEnlister);
  }
}
