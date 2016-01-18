package org.summerb.microservices.emailsender.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.summerb.microservices.emailsender.api.EmailSender;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;

import com.google.common.base.Preconditions;

public class EmailSenderSimpleQueueImpl implements EmailSender {
	private static Logger log = Logger.getLogger(EmailSenderSimpleQueueImpl.class);

	private EmailSender actualSender;
	private ExecutorService executorService;
	protected AtomicInteger pendingCount = new AtomicInteger(0);
	@SuppressWarnings("unused")
	private EmailSenderSimpleQueueMXBeanImpl mxBean;

	public EmailSenderSimpleQueueImpl(EmailSender actualSender, ExecutorService executorService) {
		Preconditions.checkArgument(actualSender != null);
		Preconditions.checkArgument(executorService != null);
		this.actualSender = actualSender;
		this.executorService = executorService;
		mxBean = new EmailSenderSimpleQueueMXBeanImpl(this);
	}

	@Override
	public void sendEmail(EmailMessage emailMessage) {
		Preconditions.checkArgument(emailMessage != null);
		try {
			pendingCount.incrementAndGet();
			executorService.submit(new SendEmailCommand(emailMessage));
		} catch (Throwable t) {
			pendingCount.decrementAndGet();
			throw new RuntimeException("Failed to enqueue message send to " + emailMessage.getToAddress(), t);
		}
	}

	public class SendEmailCommand implements Runnable {
		private EmailMessage emailMessage;

		public SendEmailCommand(EmailMessage emailMessage) {
			this.emailMessage = emailMessage;
		}

		@Override
		public void run() {
			try {
				actualSender.sendEmail(emailMessage);
			} catch (Throwable t) {
				log.error("Failed to send email message to " + emailMessage.getToAddress(), t);
			} finally {
				pendingCount.decrementAndGet();
			}
		}
	}

}
