package org.summerb.microservices.users.impl.dao;

import org.summerb.microservices.users.impl.dom.Password;

public interface PasswordDao {
	Password findPasswordByUserUuid(String uuid);

	/**
	 * Will create or update user password
	 * 
	 * @return number of affected records
	 */
	int updateUserPassword(String userUuid, String newPasswordHash);

	/**
	 * @return number of database records was updated
	 */
	int setRestorationToken(String userUuid, String restorationToken);
}
