package integr.org.summerb.easymail;

import static org.junit.Assert.assertEquals;
import integr.org.summerb.easymail.EmailSenderTransportTestImpl.StubMessage;

import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.summerb.easymail.api.EmailSender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:summerb-integr-test-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
public class EmailSenderIntegrTest {
	@Autowired
	private EmailSenderTransportTestImpl emailSenderTransportTestImpl;
	@Autowired
	private EmailSender emailSender;

	@Test
	public void testGet_expectTempalteFound() throws Exception {
		emailSenderTransportTestImpl.getMessages().clear();
		emailSender.send("testmail", Locale.ENGLISH, new MessageData());

		List<StubMessage> r = emailSenderTransportTestImpl.getMessages();
		assertEquals(1, r.size());
		assertEquals("Subject includes test username", r.get(0).message.getSubject());
		assertEquals("Body: test body", r.get(0).message.getBody());
	}

	@SuppressWarnings("unused")
	private static class MessageData {
		private final String username = "test username";
		private final String body = "test body";

		public String getUsername() {
			return username;
		}

		public String getBody() {
			return body;
		}
	}

}
