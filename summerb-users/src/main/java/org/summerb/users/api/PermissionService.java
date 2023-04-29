/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.users.api;

import java.util.List;
import java.util.Map;

/**
 * General purpose static permissions manager.
 * 
 * It capable of managing permissions on per-domain and per-subject basis, but
 * it's optional and may be ommited if not needed.
 * 
 * In certain situations permission could be treated as
 * role/group/authority/etc.
 * 
 * Although it uses same term userUuid to identify user, this is not required to
 * be the same thing used in {@link UserService}, you are free to use whenever
 * you want here.
 * 
 * NOTE! All string identifiers are limited to 45 unicode chars (that is,
 * applies to: domain name, permission name, user name, subject id)
 * 
 * @author skarpushin
 * 
 */
public interface PermissionService {
	/**
	 * 
	 * @param optionalDomainName i.e. TSACCESS
	 * @param userUuid           i.e. 12312312-3123-123-123
	 * @param optionalSubjectId  i.e. TS-123123-1231-23-123
	 * @param permissionKey      i.e. MEMBER
	 */
	void grantPermission(String optionalDomainName, String userUuid, String optionalSubjectId, String permissionKey);

	// TBD: WHISHLIST: grantPermissions(String optionalDomainName, String
	// userUuid,
	// String optionalSubjectId, List<String> permissionsKeys);

	// TBD: WHISHLIST: revokeUserPermissionsForSubject

	void revokePermission(String optionalDomainName, String userUuid, String optionalSubjectId, String permissionKey);

	void revokeUserPermissions(String optionalDomainName, String userUuid);

	void revokeAllPermissionsForSubject(String optionalDomainName, String optionalSubjectId);

	List<String> findUserPermissionsForSubject(String optionalDomainName, String userUuid, String optionalSubjectId);

	/**
	 * Get subject list where user has permissions in current domain. Optionally
	 * filtered by specific permission.
	 * 
	 * @param optionalRequiredPermission if specified will be used to filter only
	 *                                   those subjects where use has this
	 *                                   permission
	 * 
	 * @return list of subjects user has permissions for
	 */
	List<String> findSubjectsUserHasPermissionsFor(String optionalDomainName, String userUuid,
			String optionalRequiredPermission);

	/**
	 * Get list of users and their permissions for the given subject
	 * 
	 * @return list of users and their permissions for the given subject
	 */
	Map<String, List<String>> findUsersAndTheirPermissionsForSubject(String optionalDomainName,
			String optionalSubjectId);

	boolean hasPermission(String optionalDomainName, String userUuid, String optionalSubjectId, String permissionKey);
}
