package org.summerb.microservices.emailsender.impl;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class EmailSenderSimpleQueueMXBeanImpl implements EmailSenderSimpleQueueMXBean {
	private static Logger log = Logger.getLogger(EmailSenderSimpleQueueMXBeanImpl.class);
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
