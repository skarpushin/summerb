package org.summerb.microservices.users.impl.dao;

import java.util.List;
import java.util.Map;

public interface PermissionDao {
	void grantPermission(String domainName, String userUuid, String subjectId, String permissionKey);

	void revokePermission(String domainName, String userUuid, String subjectId, String permissionKey);

	void revokeUserPermissions(String domainName, String userUuid);

	void clearSubjectPermissions(String domainName, String subjectId);

	List<String> getUserPermissionsForSubject(String domainName, String userUuid, String subjectId);

	List<String> getSubjectsUserHasPermissionFor(String domainName, String userUuid);

	List<String> getSubjectsUserHasPermissionForFiltered(String domainName, String userUuid, String requiredPermission);

	Map<String, List<String>> getUsersAndTheirPermissionsForSubject(String domainName, String subjectId);

}
