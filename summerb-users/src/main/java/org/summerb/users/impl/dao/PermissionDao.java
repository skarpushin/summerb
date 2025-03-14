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
package org.summerb.users.impl.dao;

import java.util.List;
import java.util.Map;

public interface PermissionDao {
  void grantPermission(String domainName, String userUuid, String subjectId, String permissionKey);

  void revokePermission(String domainName, String userUuid, String subjectId, String permissionKey);

  void revokeUserPermissions(String domainName, String userUuid);

  void clearSubjectPermissions(String domainName, String subjectId);

  List<String> getUserPermissionsForSubject(String domainName, String userUuid, String subjectId);

  List<String> getSubjectsUserHasPermissionFor(String domainName, String userUuid);

  List<String> getSubjectsUserHasPermissionForFiltered(
      String domainName, String userUuid, String requiredPermission);

  Map<String, List<String>> getUsersAndTheirPermissionsForSubject(
      String domainName, String subjectId);
}
