/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Function;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByBuilder;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

public class EasyCrudServiceWrapper<
        TId, TRow extends HasId<TId>, TActual extends EasyCrudService<TId, TRow>>
    implements EasyCrudService<TId, TRow> {
  protected TActual actual;

  public EasyCrudServiceWrapper(TActual actual) {
    Preconditions.checkArgument(actual != null);
    this.actual = actual;
  }

  @Override
  public TRow create(TRow row) {
    return actual.create(row);
  }

  @Override
  public TRow update(TRow row) {
    return actual.update(row);
  }

  @Override
  public TRow findById(TId id) throws NotAuthorizedException {
    return actual.findById(id);
  }

  @Override
  public TRow findOneByQuery(Query<TId, TRow> query) throws NotAuthorizedException {
    return actual.findOneByQuery(query);
  }

  @Override
  public PaginatedList<TRow> find(
      PagerParams pagerParams, Query<TId, TRow> optionalQuery, OrderBy... orderBy)
      throws NotAuthorizedException {
    return actual.find(pagerParams, optionalQuery, orderBy);
  }

  @Override
  public List<TRow> findPage(
      PagerParams pagerParams, Query<TId, TRow> optionalQuery, OrderBy... orderBy) {
    return actual.findPage(pagerParams, optionalQuery, orderBy);
  }

  @Override
  public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
    actual.deleteById(id);
  }

  @Override
  public void deleteByIdOptimistic(TId id, long modifiedAt)
      throws NotAuthorizedException, EntityNotFoundException {
    actual.deleteByIdOptimistic(id, modifiedAt);
  }

  @Override
  public int deleteByQuery(Query<TId, TRow> query) throws NotAuthorizedException {
    return actual.deleteByQuery(query);
  }

  @Override
  public Class<TRow> getRowClass() {
    return actual.getRowClass();
  }

  @Override
  public EasyCrudExceptionStrategy<TId, TRow> getExceptionStrategy() {
    return actual.getExceptionStrategy();
  }

  @Override
  public String getRowMessageCode() {
    return actual.getRowMessageCode();
  }

  @Override
  public EasyCrudWireTap<TRow> getWireTap() {
    return actual.getWireTap();
  }

  @Override
  public JoinQuery<TId, TRow> buildJoinQuery(Query<TId, TRow> query) {
    return actual.buildJoinQuery(query);
  }

  @Override
  public PropertyNameResolver<TRow> getNameResolver() {
    return actual.getNameResolver();
  }

  @Override
  public TRow getById(TId id) {
    return actual.getById(id);
  }

  @Override
  public TRow getFirstByQuery(Query<TId, TRow> query, OrderBy... orderBy) {
    return actual.getFirstByQuery(query, orderBy);
  }

  @Override
  public TRow findFirstByQuery(Query<TId, TRow> query, OrderBy... orderBy) {
    return actual.findFirstByQuery(query, orderBy);
  }

  @Override
  public List<TRow> findAll(Query<TId, TRow> optionalQuery, OrderBy... orderBy) {
    return actual.findAll(optionalQuery, orderBy);
  }

  @Override
  public List<TRow> findAll(OrderBy... orderBy) {
    return actual.findAll(orderBy);
  }

  @Override
  public List<TRow> getAll(Query<TId, TRow> optionalQuery, OrderBy... orderBy) {
    return actual.getAll(optionalQuery, orderBy);
  }

  @Override
  public void delete(TRow row) {
    actual.delete(row);
  }

  @Override
  public Query<TId, TRow> query() {
    // NOTE: Instead of delegating this call to the actual service, we build instance ourselves so
    // that the query would call us instead of actual service and therefore wrapped methods will be
    // used instead of original methods
    return new Query<>(this);
  }

  @Override
  public Query<TId, TRow> query(String alias) {
    return new Query<>(this, alias);
  }

  @Override
  public int count() {
    return actual.count();
  }

  @Override
  public int count(Query<TId, TRow> optionalQuery) {
    return actual.count(optionalQuery);
  }

  @Override
  public String name(Function<TRow, ?> getter) {
    return actual.name(getter);
  }

  @Override
  public OrderByBuilder<TRow> orderBy(Function<TRow, ?> getter) {
    return actual.orderBy(getter);
  }

  @Override
  public OrderBy[] parseOrderBy(String semicolonSeparatedValues) {
    return actual.parseOrderBy(semicolonSeparatedValues);
  }

  @Override
  public OrderBy[] parseOrderBy(String[] orderByStr) {
    return actual.parseOrderBy(orderByStr);
  }

  @Override
  public TRow getOneByQuery(Query<TId, TRow> query) {
    return actual.getOneByQuery(query);
  }
}
