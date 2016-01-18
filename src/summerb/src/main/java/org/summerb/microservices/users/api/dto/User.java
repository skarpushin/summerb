package org.summerb.microservices.users.api.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class User implements Serializable {
	private static final long serialVersionUID = 1404571618064571624L;

	public static final String FN_EMAIL = "email";
	public static final int FN_EMAIL_SIZE = 45;
	public static final String FN_DISPLAY_NAME = "displayName";
	public static final int FN_DISPLAY_NAME_SIZE = 45;

	private String uuid;
	private String displayName;
	private String email;
	private String timeZone;
	private String locale;
	private long registeredAt;
	private boolean isBlocked;
	private String integrationData;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public long getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(long registeredAt) {
		this.registeredAt = registeredAt;
	}

	public boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getIntegrationData() {
		return integrationData;
	}

	public void setIntegrationData(String integrationData) {
		this.integrationData = integrationData;
	}

	@Override
	public String toString() {
		if (StringUtils.hasText(displayName)) {
			return displayName;
		}
		return "User [uuid=" + uuid + ", displayName=" + displayName + ", email=" + email + ", timeZone=" + timeZone
				+ ", locale=" + locale + "]";
	}
}
