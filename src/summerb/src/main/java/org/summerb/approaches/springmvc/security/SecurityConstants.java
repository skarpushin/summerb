package org.summerb.approaches.springmvc.security;

import org.summerb.approaches.security.api.Roles;

public class SecurityConstants extends Roles {
	/**
	 * Default Permissions Domain
	 */
	public static final String DOMAIN = "DD";

	public static final String MARKER_AWAITING_ACTIVATION = "ROLE_AWAITING_ACTIVATION";

	public static final String ROLE_CUSTOMER_CARE = "ROLE_CUSTOMER_CARE";
	public static final String ROLE_BACKGROUND_PROCESS = "ROLE_BACKGROUND_PROCESS";
}

