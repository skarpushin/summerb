package org.summerb.microservices.users.api.dto;

import java.io.Serializable;

public class AuthToken implements Serializable {
	private static final long serialVersionUID = -1633738725204366881L;

	/**
	 * Token identifier 1st part, never changes
	 */
	private String uuid;

	/**
	 * Token identifier 2nd part, expected to change after every token positive
	 * usage
	 */
	private String tokenValue;

	private String userUuid;
	private long createdAt;
	private long expiresAt;

	/**
	 * When token was last verified. Expected to change every token usage
	 */
	private long lastVerifiedAt;
	private String clientIp;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public long getLastVerifiedAt() {
		return lastVerifiedAt;
	}

	public void setLastVerifiedAt(long lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	@Override
	public String toString() {
		return "AuthToken [uuid=" + uuid + ", tokenValue=" + tokenValue + ", userUuid=" + userUuid + ", createdAt="
				+ createdAt + ", expiresAt=" + expiresAt + ", lastVerifiedAt=" + lastVerifiedAt + ", clientIp="
				+ clientIp + "]";
	}

}
