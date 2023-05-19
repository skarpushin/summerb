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

import java.io.NotSerializableException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasAuthor;
import org.summerb.easycrud.api.row.HasAutoincrementId;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.easycrud.api.row.HasUuid;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.easycrud.api.dto.Top;
import org.summerb.utils.objectcopy.Clonnable;
import org.summerb.utils.objectcopy.DeepCopy;

import com.google.common.base.Preconditions;

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

  public static final String OPTIMISTIC_LOCK_FAILED_TECH_MESSAGE =
      "Optimistic lock failed, record was already updated but someone else";
  protected static final PagerParams TOP_ONE = new Top(1);

  protected TDao dao;
  protected Class<TRow> rowClass;
  protected String rowMessageCode;
  protected EasyCrudExceptionStrategy<TId> exceptionStrategy;
  protected EasyCrudWireTap<TRow> wireTap;
  protected CurrentUserUuidResolver currentUserUuidResolver;
  protected StringIdGenerator stringIdGenerator;

  /**
   * Constructor for cases when sub-class wants to take full responsibility on instantiation
   * process.
   *
   * @deprecated when using this constructor please make sure you're properly initializing required
   *     dependencies: {@link #dao} and {@link #rowClass}
   */
  @Deprecated
  protected EasyCrudServiceImpl() {}

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
  }

  protected String buildDefaultRowMessageCode(Class<TRow> rowClass) {
    return rowClass.getCanonicalName();
  }

  protected EasyCrudWireTap<TRow> buildDefaultWireTap() {
    return new EasyCrudWireTapNoOpImpl<TRow>();
  }

  protected StringIdGeneratorUuidImpl buildDefaultStringIdGenerator() {
    return new StringIdGeneratorUuidImpl();
  }

  protected EasyCrudExceptionStrategyDefaultImpl<TId> buildDefaultEasyCrudExceptionStrategy(
      String rowMessageCode) {
    return new EasyCrudExceptionStrategyDefaultImpl<TId>(rowMessageCode);
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

  public EasyCrudExceptionStrategy<TId> getExceptionStrategy() {
    return exceptionStrategy;
  }

  /**
   * Set {@link EasyCrudExceptionStrategy}. If nothing set, then {@link
   * EasyCrudExceptionStrategyDefaultImpl} will be used
   *
   * @param exceptionStrategy exceptionStrategy
   */
  @Autowired(required = false)
  public void setExceptionStrategy(EasyCrudExceptionStrategy<TId> exceptionStrategy) {
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

  @Override
  public TRow create(TRow row) {
    try {
      Preconditions.checkArgument(row != null);

      TRow ret = copyDto(row);

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
      throw exceptionStrategy.handleExceptionAtCreate(t);
    }
  }

  @SuppressWarnings("unchecked")
  protected TRow copyDto(TRow row) {
    try {
      if (row instanceof Clonnable) {
        return ((Clonnable<TRow>) row).clone();
      }
      return DeepCopy.copyOrPopagateExcIfAny(row);
    } catch (NotSerializableException nse) {
      throw new RuntimeException(
          "Some files are not serializable. Consider implementing Clonnable interface", nse);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to clone row", t);
    }
  }

  @Override
  public TRow update(TRow newVersion) {
    try {
      Preconditions.checkArgument(newVersion != null);

      TRow currentVersion = null;
      if (wireTap.requiresOnUpdate() == EasyCrudWireTapMode.FULL_DTO_AND_CURRENT_VERSION_NEEDED) {
        currentVersion = dao.findById(newVersion.getId());
        if (currentVersion == null) {
          throw exceptionStrategy.buildNotFoundException(rowMessageCode, newVersion.getId());
        }
      }

      TRow ret = copyDto(newVersion);

      if (wireTap.requiresOnUpdate().isNeeded()) {
        wireTap.beforeUpdate(currentVersion, ret);
      }

      if (ret instanceof HasAuthor) {
        HasAuthor hasAuthor = (HasAuthor) ret;
        hasAuthor.setModifiedBy(currentUserUuidResolver.getUserUuid());
      }

      if (dao.update(ret) != 1) {
        throw exceptionStrategy.buildOptimisticLockException();
      }

      if (ret instanceof HasTimestamps) {
        HasTimestamps retTimestamps = (HasTimestamps) ret;
        HasTimestamps newTimestamps = (HasTimestamps) newVersion;

        Preconditions.checkState(
            retTimestamps.getModifiedAt() > newTimestamps.getModifiedAt(),
            "ModifiedAt expected to be increased after update");
      }

      if (wireTap.requiresOnUpdate().isNeeded()) {
        wireTap.afterUpdate(currentVersion, ret);
      }
      return ret;
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtUpdate(t);
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
      throw exceptionStrategy.handleExceptionAtDelete(t);
    }
  }

  @Override
  public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
    try {
      Preconditions.checkArgument(id != null);

      TRow existing = null;
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
      throw exceptionStrategy.handleExceptionAtDelete(t);
    }
  }

  @Override
  public void deleteByIdOptimistic(TId id, long modifiedAt)
      throws NotAuthorizedException, EntityNotFoundException {
    try {
      Preconditions.checkArgument(id != null);
      Preconditions.checkState(
          HasTimestamps.class.isAssignableFrom(rowClass),
          "Delete using optimistic lock is not allowed for DTO which doesn't support HasTimestamps");

      TRow existing = null;
      if (wireTap.requiresOnDelete().isDtoNeeded()) {
        existing = findById(id);
        if (existing == null) {
          throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
        }
      }
      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.beforeDelete(existing);
      }

      int affected = dao.delete(id, modifiedAt);
      if (affected != 1) {
        throw exceptionStrategy.buildOptimisticLockException();
      }

      if (wireTap.requiresOnDelete().isNeeded()) {
        wireTap.afterDelete(existing);
      }
    } catch (Throwable t) {
      throw exceptionStrategy.handleExceptionAtDelete(t);
    }
  }

  @Override
  public int deleteByQuery(Query query) throws NotAuthorizedException {
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
  public TRow findOneByQuery(Query query) throws NotAuthorizedException {
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
  public PaginatedList<TRow> find(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
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
  public List<TRow> findAll(Query optionalQuery, OrderBy... orderBy) {
    return find(PagerParams.ALL, optionalQuery, orderBy).getItems();
  }

  @Override
  public List<TRow> findAll(OrderBy... orderBy) {
    return find(PagerParams.ALL, null, orderBy).getItems();
  }

  @Override
  public TRow findFirstByQuery(Query query, OrderBy... orderBy) {
    PaginatedList<TRow> results = find(TOP_ONE, null, orderBy);
    if (results.getItems().isEmpty()) {
      return null;
    }
    return results.getItems().get(0);
  }

  @Override
  public TRow getFirstByQuery(Query query, OrderBy... orderBy) {
    TRow result = findFirstByQuery(query, orderBy);
    if (result == null) {
      throw new GenericEntityNotFoundException(rowMessageCode, query);
    }

    return result;
  }
}
