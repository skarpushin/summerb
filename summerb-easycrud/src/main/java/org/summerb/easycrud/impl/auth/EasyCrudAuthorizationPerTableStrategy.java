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

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

/**
 * This is abstract class for implementing Table-wide authorization logic. Table-wide means that
 * authorization is performed for general access operations regardless off row data.
 *
 * <p>You need to implement {@link #getForRead()} and {@link #getForUpdate()}. If you need to
 * customize this logic, override other methods.
 *
 * <p>If your authorization logic for Deletion and Creation is different from Update, please
 * override respective methods {@link #getForDelete()} and {@link #getForCreate()}
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
 */
public abstract class EasyCrudAuthorizationPerTableStrategy<TRow>
    extends EasyCrudWireTapAbstract<TRow> implements InitializingBean {

  protected static final NotAuthorizedResult ALLOW = null;

  @Autowired protected CurrentUserRolesResolver currentUserRolesResolver;
  @Autowired protected CurrentUserUuidResolver currentUserUuidResolver;

  protected String entityName;

  public EasyCrudAuthorizationPerTableStrategy(String entityName) {
    this.entityName = entityName;
  }

  public NotAuthorizedResult getForCreate() {
    return getForUpdate();
  }

  protected NotAuthorizedResult denyCreate() {
    return new NotAuthorizedResult(
        getUserIdentifierForException(), Permissions.CREATE, getEntityName());
  }

  public abstract NotAuthorizedResult getForRead();

  protected NotAuthorizedResult denyRead() {
    return new NotAuthorizedResult(
        getUserIdentifierForException(), Permissions.READ, getEntityName());
  }

  public abstract NotAuthorizedResult getForUpdate();

  protected NotAuthorizedResult denyUpdate() {
    return new NotAuthorizedResult(
        getUserIdentifierForException(), Permissions.UPDATE, getEntityName());
  }

  public NotAuthorizedResult getForDelete() {
    return getForUpdate();
  }

  protected NotAuthorizedResult denyDelete() {
    return new NotAuthorizedResult(
        getUserIdentifierForException(), Permissions.DELETE, getEntityName());
  }

  public boolean isAuthorizedToRead() {
    return getForRead() == ALLOW;
  }

  public boolean isAuthorizedToUpdate() {
    return getForUpdate() == ALLOW;
  }

  public boolean isAuthorizedToCreate() {
    return getForCreate() == ALLOW;
  }

  public boolean isAuthorizedToDelete() {
    return getForDelete() == ALLOW;
  }

  @Override
  public void afterPropertiesSet() {
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
    return EasyCrudWireTapMode.ONLY_INVOKE_WIRETAP;
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.ONLY_INVOKE_WIRETAP;
  }

  @Override
  public void beforeCreate(Object row) {
    NotAuthorizedResult result = getForCreate();
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void beforeRead() {
    NotAuthorizedResult result = getForRead();
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void beforeUpdate(Object from, Object to) {
    NotAuthorizedResult result = getForUpdate();
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  @Override
  public void beforeDelete(Object row) {
    NotAuthorizedResult result = getForDelete();
    if (result == ALLOW) {
      return;
    }
    throw new NotAuthorizedException(result);
  }

  protected String getUserIdentifierForException() {
    Preconditions.checkState(currentUserUuidResolver != null, "currentUserUuidResolver required");
    return currentUserUuidResolver.getUserUuid();
  }

  protected String getEntityName() {
    return entityName;
  }
}
