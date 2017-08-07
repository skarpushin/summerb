package org.summerb.utils.logging;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This class created to close {@link RollingFileAppenderRecurringExcSkip}
 * 
 * It doens't require any declaration in configuration because it's marked
 * with @WebListener and Servlet Container supposed to locate it automatically
 * 
 * @author sergeyk
 *
 */
@WebListener
public class RollingFileAppenderRecurringExcSkipCloser implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		RollingFileAppenderRecurringExcSkip instance = RollingFileAppenderRecurringExcSkip.getInstance();
		if (instance != null) {
			instance.close();
		}
	}
}
