package org.summerb.utils.spring;

import java.util.Locale;
import java.util.Properties;

public interface AllMessagesProvider {

	Properties getAllMessages(Locale locale);

	long getReloadIntervalSeconds();
}