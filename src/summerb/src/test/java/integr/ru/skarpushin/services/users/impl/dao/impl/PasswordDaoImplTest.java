package integr.ru.skarpushin.services.users.impl.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.microservices.users.api.PasswordService;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.dto.UserFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-users-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class PasswordDaoImplTest {

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private UserService userService;

	@BeforeTransaction
	public void verifyInitialDatabaseState() {
		// logic to verify the initial state before a transaction is started
	}

	@Before
	public void setUp() {
		// set up test data within the transaction
	}

	@Test
	public void testSetUserPassword_expectWillBePerformedOk() throws Exception {
		User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

		String pwd1 = "aaaa";
		passwordService.setUserPassword(createdUser.getUuid(), pwd1);

		boolean result = passwordService.isUserPasswordValid(createdUser.getUuid(), pwd1);
		assertTrue(result);
	}

	@Test
	public void testSetUserPassword_expectDuplicateWillBeRewrittenWithoutErrors() throws Exception {
		User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

		String pwd1 = "aaaa";
		passwordService.setUserPassword(createdUser.getUuid(), pwd1);

		String pwd2 = "bbbb";
		passwordService.setUserPassword(createdUser.getUuid(), pwd2);

		boolean result = passwordService.isUserPasswordValid(createdUser.getUuid(), pwd2);
		assertTrue(result);
	}

	@Test
	public void testCreateRestorationToken_expectDuplicateWillBeRewrittenWithoutErrors() throws Exception {
		User createdUser = userService.createUser(UserFactory.createNewUserTemplate());

		String pwd1 = "aaaa";
		passwordService.setUserPassword(createdUser.getUuid(), pwd1);

		String restorationToken = passwordService.getNewRestorationTokenForUser(createdUser.getUuid());

		boolean result = passwordService.isRestorationTokenValid(createdUser.getUuid(), restorationToken);
		assertTrue(result);

		passwordService.deleteRestorationToken(createdUser.getUuid());

		result = passwordService.isRestorationTokenValid(createdUser.getUuid(), restorationToken);
		assertFalse(result);
	}

}