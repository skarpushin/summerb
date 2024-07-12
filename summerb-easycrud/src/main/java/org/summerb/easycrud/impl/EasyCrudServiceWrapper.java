/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryCommands;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.api.row.HasId;
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
  public TRow findOneByQuery(QueryConditions query) throws NotAuthorizedException {
    return actual.findOneByQuery(query);
  }

  @Override
  public PaginatedList<TRow> find(
      PagerParams pagerParams, QueryConditions optionalQuery, OrderBy... orderBy)
      throws NotAuthorizedException {
    return actual.find(pagerParams, optionalQuery, orderBy);
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
  public int deleteByQuery(QueryConditions query) throws NotAuthorizedException {
    return actual.deleteByQuery(query);
  }

  @Override
  public Class<TRow> getRowClass() {
    return actual.getRowClass();
  }

  @Override
  public EasyCrudExceptionStrategy<TId> getExceptionStrategy() {
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
  public TRow getById(TId id) {
    return actual.getById(id);
  }

  @Override
  public TRow getFirstByQuery(QueryConditions query, OrderBy... orderBy) {
    return actual.getFirstByQuery(query, orderBy);
  }

  @Override
  public TRow findFirstByQuery(QueryConditions query, OrderBy... orderBy) {
    return actual.findFirstByQuery(query, orderBy);
  }

  @Override
  public List<TRow> findAll(QueryConditions optionalQuery, OrderBy... orderBy) {
    return actual.findAll(optionalQuery, orderBy);
  }

  @Override
  public List<TRow> findAll(OrderBy... orderBy) {
    return actual.findAll(orderBy);
  }

  @Override
  public List<TRow> getAll(QueryConditions optionalQuery, OrderBy... orderBy) {
    return actual.getAll(optionalQuery, orderBy);
  }

  @Override
  public void delete(TRow row) {
    actual.delete(row);
  }

  @Override
  public Query<TRow> newQuery() {
    return actual.newQuery();
  }

  @Override
  public QueryCommands<TId, TRow> query() {
    return actual.query();
  }

  @Override
  public String name(Function<TRow, ?> getter) {
    return actual.name(getter);
  }

  @Override
  public TRow getOneByQuery(QueryConditions query) {
    return actual.getOneByQuery(query);
  }
}
