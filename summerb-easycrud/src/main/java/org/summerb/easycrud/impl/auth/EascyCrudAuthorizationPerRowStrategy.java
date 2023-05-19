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
package org.summerb.easycrud.impl.auth;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

import com.google.common.base.Preconditions;

/**
 * This is abstract class for implementing Per-row authorization logic. Per-row means that
 * authorization is performed based on Row data.
 *
 * <p>You need to provide impl for getForUpdate and getForRead. If you need to customize this logic,
 * override other methods.
 *
 * <p>If your authorization logic for Deletion and Creation is different from Update, please
 * override respective methods getForCreate and getForDelete
 *
 * <p>If you do not need authorization to be performed for some of the CRUD operations you can
 * optimize performance by overriding following methods and disabling calls to this authorization
 * logic:
 *
 * <ul>
 *   <li>{@link #requiresOnCreate()}
 *   <li>{@link #requiresOnRead()}
 *   <li>{@link #requiresOnUpdate()}
 *   <li>{@link #requiresOnDelete()}
 * </ul>
 *
 * @author Sergey Karpushin
 * @param <TRow> type of row
 */
public abstract class EascyCrudAuthorizationPerRowStrategy<TRow>
    extends EasyCrudWireTapAbstract<TRow> implements InitializingBean {

  protected static final NotAuthorizedResult ALLOW = null;

  @Autowired protected CurrentUserRolesResolver currentUserRolesResolver;
  @Autowired protected CurrentUserUuidResolver currentUserUuidResolver;

  public NotAuthorizedResult getForCreate(TRow row) {
    return getForUpdate(row, row);
  }

  protected NotAuthorizedResult denyCreate(TRow row) {
    return new NotAuthorizedResult(getUserIdentifierForException(), Permissions.CREATE, getId(row));
  }

  public abstract NotAuthorizedResult getForRead(TRow row);

  protected NotAuthorizedResult denyRead(TRow row) {
    return new NotAuthorizedResult(getUserIdentifierForException(), Permissions.READ, getId(row));
  }

  public abstract NotAuthorizedResult getForUpdate(TRow persistedVersion, TRow row);

  protected NotAuthorizedResult denyUpdate(TRow row) {
    return new NotAuthorizedResult(getUserIdentifierForException(), Permissions.UPDATE, getId(row));
  }

  public NotAuthorizedResult getForDelete(TRow row) {
    return getForUpdate(row, row);
  }

  protected NotAuthorizedResult denyDelete(TRow row) {
    return new NotAuthorizedResult(getUserIdentifierForException(), Permissions.DELETE, getId(row));
  }

  public boolean isAuthorizedToRead(TRow row) {
    return getForRead(row) == ALLOW;
  }

  public boolean isAuthorizedToUpdate(TRow persistedVersion, TRow row) {
    return getForUpdate(persistedVersion, row) == ALLOW;
  }

  public boolean isAuthorizedToCreate(TRow row) {
    return getForCreate(row) == ALLOW;
  }

  public boolean isAuthorizedToDelete(TRow row) {
    return getForDelete(row) == ALLOW;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Preconditions.checkArgument(
        currentUserRolesResolver != null, "currentUserRolesResolver required");
    Preconditions.checkArgument(
        currentUserUuidResolver != null, "currentUserUuidResolver required");
  }

  @Override
  public boolean requiresOnCreate() {
    return true;
  }

  @Override
  public boolean requiresOnRead() {
    return true;
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public void beforeCreate(TRow row) {
    NotAuthorizedResult result = getForCreate(row);
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void afterRead(TRow row) {
    NotAuthorizedResult result = getForRead(row);
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {
    NotAuthorizedResult result = getForUpdate(from, to);
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void beforeDelete(TRow row) {
    NotAuthorizedResult result = getForDelete(row);
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  protected String getUserIdentifierForException() {
    Preconditions.checkArgument(
        currentUserUuidResolver != null, "currentUserUuidResolver required");
    return currentUserUuidResolver.getUserUuid();
  }

  @SuppressWarnings("rawtypes")
  protected String getId(TRow row) {
    if (row == null) {
      return "n/a";
    }
    if (row instanceof HasId) {
      return row.getClass().getSimpleName() + "#" + String.valueOf(((HasId) row).getId());
    }
    return row.getClass().getSimpleName() + "#?";
  }
}
