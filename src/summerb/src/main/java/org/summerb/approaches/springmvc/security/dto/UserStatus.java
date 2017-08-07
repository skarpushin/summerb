package org.summerb.approaches.springmvc.security.dto;

public enum UserStatus {
	NotExists,

	/**
	 * We have only record in user registry. Just email
	 */
	Provisioned,

	/**
	 * User in registration process. Awaiting registration confirmation
	 */
	AwaitingActivation,

	/**
	 * User has ROLE_USER
	 */
	NormalUser
}
