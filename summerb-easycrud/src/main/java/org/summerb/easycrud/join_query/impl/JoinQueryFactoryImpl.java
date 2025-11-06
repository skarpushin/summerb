package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinQueryFactory;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.FieldsEnlister;

public class JoinQueryFactoryImpl implements JoinQueryFactory {
  protected ReferringToFieldsFinder referringToFieldsFinder;
  protected SelectFactory selectFactory;
  protected QuerySpecificsResolver querySpecificsResolver;
  protected FieldsEnlister fieldsEnlister;

  public JoinQueryFactoryImpl(
      ReferringToFieldsFinder referringToFieldsFinder,
      SelectFactory selectFactory,
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister) {
    Preconditions.checkArgument(
        referringToFieldsFinder != null, "referringToFieldsFinder is required");
    Preconditions.checkArgument(selectFactory != null, "selectFactory is required");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver is required");
    Preconditions.checkArgument(fieldsEnlister != null, "fieldsEnlister is required");

    this.fieldsEnlister = fieldsEnlister;
    this.referringToFieldsFinder = referringToFieldsFinder;
    this.selectFactory = selectFactory;
    this.querySpecificsResolver = querySpecificsResolver;
  }

  @Override
  public <TId, TRow extends HasId<TId>> JoinQuery<TId, TRow> build(Query<TId, TRow> primaryQuery) {
    return new JoinQueryImpl<>(
        primaryQuery,
        referringToFieldsFinder,
        selectFactory,
        querySpecificsResolver,
        fieldsEnlister);
  }
}
