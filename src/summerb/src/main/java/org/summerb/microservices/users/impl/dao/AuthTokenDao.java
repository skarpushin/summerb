package org.summerb.microservices.users.impl.dao;

import java.util.List;

import org.summerb.microservices.users.api.dto.AuthToken;

public interface AuthTokenDao {

	void createAuthToken(AuthToken authToken);

	AuthToken findAuthTokenByUuid(String authTokenUuid);

	void updateToken(String authTokenUuid, long now, String newTokenValue);

	void deleteAuthToken(String authTokenUuid);

	List<AuthToken> findAuthTokensByUser(String userUuid);

}
