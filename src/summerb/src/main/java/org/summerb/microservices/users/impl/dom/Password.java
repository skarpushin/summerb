package org.summerb.microservices.users.impl.dom;

public class Password {
	private String userUuid;
	private String passwordHash;
	private String restorationToken;

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRestorationToken() {
		return restorationToken;
	}

	public void setRestorationToken(String restorationTokenUuid) {
		this.restorationToken = restorationTokenUuid;
	}
}
