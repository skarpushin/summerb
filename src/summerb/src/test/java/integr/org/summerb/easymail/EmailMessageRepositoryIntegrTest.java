package integr.org.summerb.easymail;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.summerb.easymail.api.EmailMessageRepository;
import org.summerb.easymail.api.dto.EmailMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:summerb-integr-test-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
public class EmailMessageRepositoryIntegrTest {
	@Autowired
	private EmailMessageRepository emailMessageRepository;

	@Test
	public void testGet_expectTempalteFound() throws Exception {
		EmailMessage r = emailMessageRepository.get("testmail", Locale.ENGLISH);
		assertNotNull(r);
		assertEquals("Subject includes ${username}", r.getSubject());
	}
}
