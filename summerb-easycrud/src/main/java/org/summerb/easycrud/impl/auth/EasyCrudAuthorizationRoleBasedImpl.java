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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.summerb.security.api.dto.NotAuthorizedResult;

/**
 * Simple authorization logic which allows access based on user roles.
 *
 * <p>You can instantiate it via {@link #EasyCrudAuthorizationRoleBasedImpl(Collection, Collection)}
 * and specify to set of roles - for Reading and for Modification
 *
 * <p>You also can do simpler option -- provide one set of roles which both will be used for
 * checking permission for all 4 CRUD operations
 *
 * @author Sergey Karpushin
 */
public class EasyCrudAuthorizationRoleBasedImpl extends EascyCrudAuthorizationPerTableStrategy
    implements InitializingBean {

  private Set<String> rolesAuthorizedToRead;
  private Set<String> rolesAuthorizedToModify;

  /**
   * @param rolesAuthorizedToRead which Roles are allowed to Read data from this service
   * @param rolesAuthorizedToModify which Roles are allowed to Modify (Create, Update, Delete) data
   *     from this service
   */
  public EasyCrudAuthorizationRoleBasedImpl(
      String entityName,
      Collection<String> rolesAuthorizedToRead,
      Collection<String> rolesAuthorizedToModify) {
    super(entityName);
    this.rolesAuthorizedToRead =
        rolesAuthorizedToRead == null ? null : new HashSet<>(rolesAuthorizedToRead);
    this.rolesAuthorizedToModify =
        rolesAuthorizedToModify == null ? null : new HashSet<>(rolesAuthorizedToModify);
  }

  /** @param authorizedRoles which Roles are allowed to Read and Update data from this service */
  public EasyCrudAuthorizationRoleBasedImpl(String entityName, Collection<String> authorizedRoles) {
    super(entityName);
    this.rolesAuthorizedToRead = authorizedRoles == null ? null : new HashSet<>(authorizedRoles);
    this.rolesAuthorizedToModify = this.rolesAuthorizedToRead;
  }

  @Override
  public NotAuthorizedResult getForRead() {
    if (rolesAuthorizedToRead == null
        || currentUserRolesResolver.hasAnyRole(rolesAuthorizedToRead)) {
      return ALLOW;
    }
    return denyRead();
  }

  @Override
  public NotAuthorizedResult getForUpdate() {
    if (rolesAuthorizedToModify == null
        || currentUserRolesResolver.hasAnyRole(rolesAuthorizedToModify)) {
      return ALLOW;
    }
    return denyUpdate();
  }
}
