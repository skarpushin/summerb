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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.summerb.email.api.EmailSender;
import org.summerb.email.api.dto.EmailMessage;

import com.google.common.base.Preconditions;

public class EmailSenderSimpleQueueImpl implements EmailSender {
	private static Logger log = LogManager.getLogger(EmailSenderSimpleQueueImpl.class);

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
