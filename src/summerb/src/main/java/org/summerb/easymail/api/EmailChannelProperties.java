package org.summerb.easymail.api;

import java.util.Properties;

import javax.mail.Authenticator;

public interface EmailChannelProperties {
	Properties getProperties();

	Authenticator getAuthenticator();
}
