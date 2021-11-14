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
package org.summerb.users.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.users.impl.dao.PermissionDao;

import com.google.common.base.Preconditions;

public class PermissionServiceImpl implements PermissionService {
	private static final String DEFAULT = "default";
	private static Logger log = LogManager.getLogger(PermissionServiceImpl.class);

	private PermissionDao permissionDao;

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void grantPermission(String optionalDomainName, String userUuid, String optionalSubjectId,
			String permissionKey) {
		Preconditions.checkArgument(StringUtils.hasText(permissionKey));
		Preconditions.checkArgument(StringUtils.hasText(userUuid));
		String domainName = getOptionalParamValue(optionalDomainName);
		String subjectId = getOptionalParamValue(optionalSubjectId);

		try {
			permissionDao.grantPermission(domainName, userUuid, subjectId, permissionKey);
		} catch (DuplicateKeyException dke) {
			// it's ok
			log.debug("Duplicate key exception sealed. Looks like same permission is already granted.", dke);
		} catch (Throwable t) {
			String msg = String.format("Failed to grant permission '%s' to user '%s' on subject '%s' in domain '%s'",
					permissionKey, userUuid, subjectId, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void revokePermission(String optionalDomainName, String userUuid, String optionalSubjectId,
			String permissionKey) {
		Preconditions.checkArgument(StringUtils.hasText(permissionKey));
		Preconditions.checkArgument(StringUtils.hasText(userUuid));
		String domainName = getOptionalParamValue(optionalDomainName);
		String subjectId = getOptionalParamValue(optionalSubjectId);

		try {
			permissionDao.revokePermission(domainName, userUuid, subjectId, permissionKey);
		} catch (Throwable t) {
			String msg = String.format(
					"Failed to revoke permission '%s' from user '%s' for subject '%s' in domain '%s'", permissionKey,
					userUuid, subjectId, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void revokeUserPermissions(String optionalDomainName, String userUuid) {
		Preconditions.checkArgument(StringUtils.hasText(userUuid));
		String domainName = getOptionalParamValue(optionalDomainName);

		try {
			permissionDao.revokeUserPermissions(domainName, userUuid);
		} catch (Throwable t) {
			String msg = String.format("Failed to revoke user '%s' permissions in domain '%s'", userUuid, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void revokeAllPermissionsForSubject(String optionalDomainName, String optionalSubjectId) {
		String domainName = getOptionalParamValue(optionalDomainName);
		String subjectId = getOptionalParamValue(optionalSubjectId);

		try {
			permissionDao.clearSubjectPermissions(domainName, subjectId);
		} catch (Throwable t) {
			String msg = String.format("Failed to clear subject '%s' permissions in domain '%s'", subjectId,
					domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	public List<String> findUserPermissionsForSubject(String optionalDomainName, String userUuid,
			String optionalSubjectId) {
		Preconditions.checkArgument(StringUtils.hasText(userUuid));
		String domainName = getOptionalParamValue(optionalDomainName);
		String subjectId = getOptionalParamValue(optionalSubjectId);

		try {
			return permissionDao.getUserPermissionsForSubject(domainName, userUuid, subjectId);
		} catch (Throwable t) {
			String msg = String.format("Failed to get user '%s' permissions for subject '%s' in domain '%s'", userUuid,
					subjectId, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	public boolean hasPermission(String optionalDomainName, String userUuid, String optionalSubjectId,
			String permissionKey) {
		List<String> subjectPermissions = findUserPermissionsForSubject(optionalDomainName, userUuid,
				optionalSubjectId);
		return subjectPermissions.contains(permissionKey);
	}

	@Override
	public List<String> findSubjectsUserHasPermissionsFor(String optionalDomainName, String userUuid,
			String optionalRequiredPermission) {
		Preconditions.checkArgument(StringUtils.hasText(userUuid));
		String domainName = getOptionalParamValue(optionalDomainName);

		try {
			if (!StringUtils.hasText(optionalRequiredPermission)) {
				return permissionDao.getSubjectsUserHasPermissionFor(domainName, userUuid);
			} else {
				return permissionDao.getSubjectsUserHasPermissionForFiltered(domainName, userUuid,
						optionalRequiredPermission);
			}
		} catch (Throwable t) {
			String msg = String.format(
					"Failed to get subject list user '%s' has permissions (optional = '%s') for in domain '%s'",
					userUuid, optionalRequiredPermission, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	public Map<String, List<String>> findUsersAndTheirPermissionsForSubject(String optionalDomainName,
			String optionalSubjectId) {
		String domainName = getOptionalParamValue(optionalDomainName);
		String subjectId = getOptionalParamValue(optionalSubjectId);

		try {
			return permissionDao.getUsersAndTheirPermissionsForSubject(domainName, subjectId);
		} catch (Throwable t) {
			String msg = String.format("Failed to get users and their permissions for subject '%s' in domain '%s'",
					subjectId, domainName);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	private String getOptionalParamValue(String optionalParameterValue) {
		if (StringUtils.hasText(optionalParameterValue)) {
			return optionalParameterValue;
		}

		return DEFAULT;
	}

	public PermissionDao getPermissionDao() {
		return permissionDao;
	}

	public void setPermissionDao(PermissionDao permissionDao) {
		this.permissionDao = permissionDao;
	}

}
