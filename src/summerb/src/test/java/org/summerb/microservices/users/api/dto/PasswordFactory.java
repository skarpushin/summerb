package org.summerb.microservices.users.api.dto;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.summerb.microservices.users.impl.dom.Password;

public class PasswordFactory {
	public static PasswordEncoder passwordEncoder = new StandardPasswordEncoder("test");
	public static final String RIGHT_PASSWORD_FOR_EXISTENT_USER = "passwordRight";
	public static String RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH;
	public static final String TOKEN_FOR_EXISTENT_USER = UUID.randomUUID().toString();
	public static final String NOT_EXISTENT_RESTORATION_TOKEN = UUID.randomUUID().toString();

	static {
		RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH = passwordEncoder.encode(RIGHT_PASSWORD_FOR_EXISTENT_USER);
	}

	private PasswordFactory() {

	}

	public static Password createExistentUserPassword() {
		Password ret = new Password();
		ret.setUserUuid(UserFactory.EXISTENT_USER);
		ret.setPasswordHash(RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH);
		ret.setRestorationToken(TOKEN_FOR_EXISTENT_USER);
		return ret;
	}

}