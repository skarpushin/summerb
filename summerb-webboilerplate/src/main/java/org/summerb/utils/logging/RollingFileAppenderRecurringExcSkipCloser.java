/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
