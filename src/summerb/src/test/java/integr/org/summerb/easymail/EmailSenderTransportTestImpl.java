package integr.org.summerb.easymail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.summerb.easymail.api.EmailSenderTransport;
import org.summerb.easymail.api.dto.EmailMessage;
import org.summerb.easymail.api.dto.EmailRecipient;

import com.google.common.base.Preconditions;

public class EmailSenderTransportTestImpl implements EmailSenderTransport {
	private Logger log = Logger.getLogger(getClass());
	private final List<StubMessage> messages = new ArrayList<StubMessage>();

	@Override
	public void send(EmailMessage emailMessage, EmailRecipient to) {
		try {
			Preconditions.checkArgument(emailMessage != null);
			// Real sending "Transport.send(message)" is omitted because of stub
			log.trace("Sent email, Subject: " + emailMessage.getSubject() + ", Body: " + emailMessage.getBody());
			messages.add(new StubMessage(to, emailMessage));
		} catch (Throwable t) {
			throw new RuntimeException("Failed to send email", t);
		}
	}

	public List<StubMessage> getMessages() {
		return messages;
	}

	public static class StubMessage {
		public final EmailRecipient to;
		public final EmailMessage message;

		public StubMessage(EmailRecipient to, EmailMessage message) {
			this.to = to;
			this.message = message;
		}
	}
}
