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
package org.summerb.email.impl;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailSenderSimpleQueueMXBeanImpl implements EmailSenderSimpleQueueMXBean {
	private static Logger log = LogManager.getLogger(EmailSenderSimpleQueueMXBeanImpl.class);
	private EmailSenderSimpleQueueImpl queue;

	public EmailSenderSimpleQueueMXBeanImpl(EmailSenderSimpleQueueImpl queue) {
		this.queue = queue;
		registerMxBean();
	}

	private void registerMxBean() {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			String name = String.format("%s:type=Queue,name=%s", queue.getClass().getPackage().getName(),
					"EMailSender");
			ObjectName mxBeanName = new ObjectName(name);
			if (!server.isRegistered(mxBeanName)) {
				server.registerMBean(this, new ObjectName(name));
			}
		} catch (Throwable t) {
			log.error("Failed to init jmx bean for EMailSender", t);
		}
	}

	@Override
	public int getPendingCount() {
		return queue.pendingCount.get();
	}
}
