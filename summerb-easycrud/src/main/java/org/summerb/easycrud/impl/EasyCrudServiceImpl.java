/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.easycrud.api.RowCloner;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryCommands;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.api.row.HasAuthor;
import org.summerb.easycrud.api.row.HasAutoincrementId;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.easycrud.api.row.HasUuid;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;

/**
 * Default impl of EasyCrudService, with focus on OOD:OCP principle. In case some logic needs to be
 * changed you can do it via {@link EasyCrudWireTap} interface - you don't need to write another
 * impl of {@link EasyCrudService} each time you need to change it's behavior.
 *
 * <p>If your Row implements {@link HasAuthor} interface then this impl requires {@link
 * CurrentUserUuidResolver} to be injected.
 *
 * <p>If your Row implements {@link HasUuid} interface then this impl requires {@link
 * StringIdGenerator} to be injected.
 *
 * @author sergeyk
 * @param <TId> type of id
 * @param <TRow> type of dto (must have {@link HasId} interface
 * @param <TDao> type of dao, must be a subclass of {@link EasyCrudDao}
 */
public class EasyCrudServiceImpl<TId, TRow extends HasId<TId>, TDao extends EasyCrudDao<TId, TRow>>
    implements EasyCrudService<TId, TRow>, InitializingBean {

  protected static final PagerParams TOP_ONE = new Top(1);

  protected TDao dao;
  protected Class<TRow> rowClass;
  protected String rowMessageCode;
  protected EasyCrudExceptionStrategy<TId, TRow> exceptionStrategy;
  protected EasyCrudWireTap<TRow> wireTap;
  protected CurrentUserUuidResolver currentUserUuidResolver;
  protected StringIdGenerator stringIdGenerator;
  protected PropertyNameResolverFactory propertyNameResolverFactory;
  protected PropertyNameResolver<TRow> nameResolver;
  protected RowCloner rowCloner;

  /**
   * Constructor for cases when subclass wants to take full responsibility on instantiation process.
   *
   * <p>When using this constructor please make sure you're properly initializing required
   * dependencies: {@link #dao} and {@link #rowClass}
   */
  protected EasyCrudServiceImpl() {}

  /**
   * This is also for subclasses, mostly for cases when used in conunction with {@link
   * org.summerb.easycrud.scaffold.api.EasyCrudScaffold#fromService(Class, EasyCrudServiceImpl,
   * String, Object...)}
   */
  protected EasyCrudServiceImpl(Class<TRow> rowClass) {
    this.rowClass = rowClass;
  }

  public EasyCrudServiceImpl(TDao dao, Class<TRow> rowClass) {
    this.dao = dao;
    this.rowClass = rowClass;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Preconditions.checkArgument(dao != null, "dao is required");
    Preconditions.checkArgument(rowClass != null, "rowClass is required");

    Preconditions.checkState(
        !HasAuthor.class.isAssignableFrom(rowClass) || currentUserUuidResolver != null,
        "CurrentUserUuidResolver required for those DTOs who implement HasAuthor");

    if (!StringUtils.hasText(rowMessageCode)) {
      rowMessageCode = buildDefaultRowMessageCode(this.rowClass);
    }

    if (wireTap == null) {
      wireTap = buildDefaultWireTap();
    }

    if (exceptionStrategy == null) {
      exceptionStrategy = buildDefaultEasyCrudExceptionStrategy(this.rowMessageCode);
    }

    if (stringIdGenerator == null && HasUuid.class.isAssignableFrom(rowClass)) {
      stringIdGenerator = buildDefaultStringIdGenerator();
    }

    if (rowCloner == null) {
      rowCloner = new RowClonerReflectionImpl();
      // rowCloner = new RowClonerDeepCopyImpl();
    }
  }

  protected String buildDefaultRowMessageCode(Class<TRow> rowClass) {
    return rowClass.getCanonicalName();
  }

  protected EasyCrudWireTap<TRow> buildDefaultWireTap() {
    return new EasyCrudWireTapNoOpImpl<>();
  }

  protected StringIdGeneratorUuidImpl buildDefaultStringIdGenerator() {
    return new StringIdGeneratorUuidImpl();
  }

  protected EasyCrudExceptionStrategyDefaultImpl<TId, TRow> buildDefaultEasyCrudExceptionStrategy(
      String rowMessageCode) {
    return new EasyCrudExceptionStrategyDefaultImpl<>(rowMessageCode);
  }

  public TDao getDao() {
    return dao;
  }

  public CurrentUserUuidResolver getCurrentUserResolver() {
    return currentUserUuidResolver;
  }

  /**
   * Set CurrentUserUuidResolver. This is mandatory and will be used only if Row class implements
   * {@link HasAuthor}. In other cases you don't need to set anything here
   *
   * @param currentUserUuidResolver currentUserUuidResolver
   */
  @Autowired(required = false)
  public void setCurrentUserResolver(CurrentUserUuidResolver currentUserUuidResolver) {
    Preconditions.checkState(rowClass != null, "please set rowClass before callign this method");
    Preconditions.checkArgument(
        !HasAuthor.class.isAssignableFrom(rowClass) || currentUserUuidResolver != null,
        "CurrentUserUuidResolver required for those DTOs who implement HasAuthor");
    this.currentUserUuidResolver = currentUserUuidResolver;
  }

  @Override
  public Class<TRow> getRowClass() {
    return rowClass;
  }

  @Override
  public EasyCrudExceptionStrategy<TId, TRow> getExceptionStrategy() {
    return exceptionStrategy;
  }

  /**
   * Set {@link EasyCrudExceptionStrategy}. If nothing set, then {@link
   * EasyCrudExceptionStrategyDefaultImpl} will be used
   *
   * @param exceptionStrategy exceptionStrategy
   */
  @Autowired(required = false)
  public void setExceptionStrategy(EasyCrudExceptionStrategy<TId, TRow> exceptionStrategy) {
    Preconditions.checkArgument(exceptionStrategy != null, "exceptionStrategy required");
    this.exceptionStrategy = exceptionStrategy;
  }

  @Override
  public String getRowMessageCode() {
    return rowMessageCode;
  }

  public void setRowMessageCode(String rowMessageCode) {
    Preconditions.checkArgument(StringUtils.hasText(rowMessageCode), "rowMessageCode required");
    this.rowMessageCode = rowMessageCode;
  }

  @Override
  public EasyCrudWireTap<TRow> getWireTap() {
    return wireTap;
  }

  /**
   * Set {@link EasyCrudWireTap}. If nothing set, then NoOp impl will be used {@link
   * EasyCrudWireTapNoOpImpl}
   *
   * @param wireTap wireTap
   */
  public void setWireTap(EasyCrudWireTap<TRow> wireTap) {
    Preconditions.checkArgument(wireTap != null, "wireTap required");
    this.wireTap = wireTap;
  }

  public StringIdGenerator getStringIdGenerator() {
    return stringIdGenerator;
  }

  /**
   * Set {@link StringIdGenerator}. This is mandatory only when Row class implements {@link
   * HasUuid}. If not set, then {@link StringIdGeneratorUuidImpl} will be used by default.
   *
   * @param stringIdGenerator stringIdGenerator
   */
  public void setStringIdGenerator(StringIdGenerator stringIdGenerator) {
    Preconditions.checkState(rowClass != null, "please set rowClass before callign this method");
    Preconditions.checkArgument(
        !HasUuid.class.isAssignableFrom(rowClass) || stringIdGenerator != null,
        "stringIdGenerator required");
    this.stringIdGenerator = stringIdGenerator;
  }

  public PropertyNameResolverFactory getPropertyNameResolverFactory() {
    return propertyNameResolverFactory;
  }

  @Autowired(required = false)
  public void setPropertyNameResolverFactory(
      PropertyNameResolverFactory propertyNameResolverFactory) {
    this.propertyNameResolverFactory = propertyNameResolverFactory;
  }

  @Override
  public TRow create(TRow row) {
    TRow ret = null;
    try {
      Preconditions.checkArgument(row != null);

      ret = copyDto(row);

      if (wireTap.requiresOnCreate()) {
        wireTap.beforeCreate(ret);
      }

      if (ret instanceof HasAuthor) {
        HasAuthor hasAuthor = (HasAuthor) ret;
        String currentUserUuid = currentUserUuidResolver.getUserUuid();
        hasAuthor.setCreatedBy(currentUserUuid);
        hasAuthor.setModifiedBy(currentUserUuid);
      }

      dao.create(ret);

      if (ret instanceof HasAutoincrementId) {
        Preconditions.checkState(
            ((HasAutoincrementId) ret).getId() != null,
            "For DTO with HasAutoincrementId id field expected to be filled after creation");
      }

      if (ret instanceof HasUuid) {
        Preconditions.checkState(
            stringIdGenerator.isValidId(((HasUuid) ret).getId()),
            "For DTO with HasUuid id field expected to be filled after creation");
      }

      if (wireTap.requiresOnCreate()) {
        wireTap.afterCreate(ret);
      }
      return ret;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtCreate(t, ret == null ? row : ret);
    }
  }

  @SuppressWarnings("unchecked")
  protected TRow copyDto(TRow row) {
    return (TRow) rowCloner.clone(row);
  }

  @Override
  public TRow update(TRow newVersion) {
    TRow ret = null;
    try {
      Preconditions.checkArgument(newVersion != null);

      TRow currentVersion = null;
      if (wireTap.requiresOnUpdate() == EasyCrudWireTapMode.FULL_DTO_AND_CURRENT_VERSION_NEEDED) {
        currentVersion = dao.findById(newVersion.getId());
        if (currentVersion == null) {
          throw exceptionStrategy.buildNotFoundException(rowMessageCode, newVersion.getId());
        }
      }

      ret = copyDto(newVersion);

      if (wireTap.requiresOnUpdate().isNeeded()) {
        wireTap.beforeUpdate(currentVersion, ret);
      }

      if (ret instanceof HasAuthor) {
        HasAuthor hasAuthor = (HasAuthor) ret;
        hasAuthor.setModifiedBy(currentUserUuidResolver.getUserUuid());
      }

      dao.update(ret);

      if (wireTap.requiresOnUpdate().isNeeded()) {
        wireTap.afterUpdate(currentVersion, ret);
      }
      return ret;
    } catch (JdbcUpdateAffectedIncorrectNumberOfRowsException t) {
      throw exceptionStrategy.handleAffectedIncorrectNumberOfRowsOnUpdate(t, ret);
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtUpdate(t, ret == null ? newVersion : ret);
    }
  }

  @Override
  public void delete(TRow row) {
    try {
      Preconditions.checkArgument(row != null);
      Preconditions.checkArgument(row.getId() != null);

      if (row instanceof HasTimestamps) {
        deleteByIdOptimistic(row.getId(), ((HasTimestamps) row).getModifiedAt());
      } else {
        deleteById(row.getId());
      }
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtDelete(t, row == null ? null : row.getId(), row);
    }
  }

  @Override
  public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
    TRow existing = null;
    try {
      Preconditions.checkArgument(id != null);

      if (wireTap.requiresOnDelete().isDtoNeeded()) {
        existing = findById(id);
        if (existing == null) {
          throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
        }
      }

      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.beforeDelete(existing);
      }

      int affected = dao.delete(id);
      if (affected != 1) {
        throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
      }

      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.afterDelete(existing);
      }
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtDelete(t, id, existing);
    }
  }

  @Override
  public void deleteByIdOptimistic(TId id, long modifiedAt)
      throws NotAuthorizedException, EntityNotFoundException {
    TRow existing = null;
    try {
      Preconditions.checkArgument(id != null);
      Preconditions.checkState(
          HasTimestamps.class.isAssignableFrom(rowClass),
          "Delete using optimistic lock is not allowed for DTO which doesn't support HasTimestamps");

      if (wireTap.requiresOnDelete().isDtoNeeded()) {
        existing = findById(id);
        if (existing == null) {
          throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
        }
      }
      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.beforeDelete(existing);
      }

      dao.delete(id, modifiedAt);

      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.afterDelete(existing);
      }
    } catch (JdbcUpdateAffectedIncorrectNumberOfRowsException t) {
      throw exceptionStrategy.handleAffectedIncorrectNumberOfRowsOnDelete(t, existing);
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtDelete(t, id, existing);
    }
  }

  @Override
  public int deleteByQuery(QueryConditions query) throws NotAuthorizedException {
    try {
      Preconditions.checkArgument(query != null);

      if (wireTap.requiresOnDelete() == EasyCrudWireTapMode.NOT_APPLICABLE) {
        return dao.deleteByQuery(query);
      }

      if (wireTap.requiresOnDelete() == EasyCrudWireTapMode.ONLY_INVOKE_WIRETAP) {
        wireTap.beforeDelete(null);
        int ret = dao.deleteByQuery(query);
        wireTap.afterDelete(null);
        return ret;
      }

      // TZD: Add special handling for case when only ID is needed - so we load IDs only and do not
      // load full DTOs

      List<TRow> toDelete = findAll(query);
      int deleted = 0;
      for (TRow row : toDelete) {
        wireTap.beforeDelete(row);
        if (row instanceof HasTimestamps) {
          int affected = dao.delete(row.getId(), ((HasTimestamps) row).getModifiedAt());
          if (affected == 1) {
            deleted++;
            wireTap.afterDelete(row);
          }
        } else {
          int affected = dao.delete(row.getId());
          if (affected == 1) {
            deleted++;
            wireTap.afterDelete(row);
          }
        }
      }

      return deleted;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtDeleteByQuery(t);
    }
  }

  @Override
  public TRow findById(TId id) throws NotAuthorizedException {
    try {
      Preconditions.checkArgument(id != null);
      if (wireTap.requiresOnRead()) {
        wireTap.beforeRead();
      }

      TRow ret = dao.findById(id);
      if (ret != null && wireTap.requiresOnRead()) {
        wireTap.afterRead(ret);
      }
      return ret;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  @Override
  public TRow getById(TId id) {
    TRow ret = findById(id);
    if (ret == null) {
      throw new GenericEntityNotFoundException(rowMessageCode, id);
    }
    return ret;
  }

  @Override
  public TRow findOneByQuery(QueryConditions query) throws NotAuthorizedException {
    try {
      Preconditions.checkArgument(query != null);
      if (wireTap.requiresOnRead()) {
        wireTap.beforeRead();
      }

      TRow ret = dao.findOneByQuery(query);
      if (ret != null && wireTap.requiresOnRead()) {
        wireTap.afterRead(ret);
      }
      return ret;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  @Override
  public TRow getOneByQuery(QueryConditions query) {
    TRow ret = findOneByQuery(query);
    if (ret == null) {
      throw new GenericEntityNotFoundException(rowMessageCode, query);
    }
    return ret;
  }

  @Override
  public PaginatedList<TRow> find(
      PagerParams pagerParams, QueryConditions optionalQuery, OrderBy... orderBy)
      throws NotAuthorizedException {
    try {
      Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
      if (wireTap.requiresOnRead()) {
        wireTap.beforeRead();
      }

      PaginatedList<TRow> ret = dao.query(pagerParams, optionalQuery, orderBy);
      if (wireTap.requiresOnRead()) {
        for (TRow dto : ret.getItems()) {
          wireTap.afterRead(dto);
        }
      }
      return ret;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtFind(t);
    }
  }

  @Override
  public List<TRow> findAll(QueryConditions optionalQuery, OrderBy... orderBy) {
    return find(PagerParams.ALL, optionalQuery, orderBy).getItems();
  }

  @Override
  public List<TRow> findAll(OrderBy... orderBy) {
    return find(PagerParams.ALL, null, orderBy).getItems();
  }

  @Override
  public TRow findFirstByQuery(QueryConditions query, OrderBy... orderBy) {
    PaginatedList<TRow> results = find(TOP_ONE, query, orderBy);
    if (results.getItems().isEmpty()) {
      return null;
    }
    return results.getItems().get(0);
  }

  @Override
  public TRow getFirstByQuery(QueryConditions query, OrderBy... orderBy) {
    TRow result = findFirstByQuery(query, orderBy);
    if (result == null) {
      throw new GenericEntityNotFoundException(rowMessageCode, query);
    }

    return result;
  }

  @Override
  public List<TRow> getAll(QueryConditions optionalQuery, OrderBy... orderBy) {
    List<TRow> ret = findAll(optionalQuery, orderBy);
    if (CollectionUtils.isEmpty(ret)) {
      throw new GenericEntityNotFoundException(rowMessageCode, optionalQuery);
    }

    return ret;
  }

  @Override
  public Query<TRow> newQuery() {
    return Query.n(rowClass);
  }

  @Override
  public QueryCommands<TId, TRow> query() {
    return new QueryCommands<>(getNameResolver(), this);
  }

  @Override
  public String name(Function<TRow, ?> getter) {
    return getNameResolver().resolve(getter);
  }

  @Override
  public OrderByBuilder<TRow> orderBy(Function<TRow, ?> getter) {
    return new OrderByBuilder<>(getNameResolver(), getter);
  }

  protected PropertyNameResolver<TRow> getNameResolver() {
    if (nameResolver == null) {
      Preconditions.checkState(
          propertyNameResolverFactory != null,
          "propertyNameResolverFactory is required for this method to work");
      nameResolver = propertyNameResolverFactory.getResolver(rowClass);
    }
    return nameResolver;
  }

  public void setDao(TDao dao) {
    this.dao = dao;
  }

  public RowCloner getRowCloner() {
    return rowCloner;
  }

  @Autowired(required = false)
  public void setRowCloner(RowCloner rowCloner) {
    this.rowCloner = rowCloner;
  }
}
