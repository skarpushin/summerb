package integr.ru.skarpushin.services.users.impl.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

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
import org.summerb.microservices.users.api.AuthTokenService;
import org.summerb.microservices.users.api.PasswordService;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.AuthToken;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.dto.UserFactory;
import org.summerb.microservices.users.impl.dao.AuthTokenDao;
import org.summerb.microservices.users.impl.dao.PasswordDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-users-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class AuthTokenDaoImplTest {
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private AuthTokenDao authTokenDao;

	@Autowired
	private PasswordDao passwordDao;

	@BeforeTransaction
	public void verifyInitialDatabaseState() {
		// logic to verify the initial state before a transaction is started
	}

	@Before
	public void setUp() {
		// set up test data within the transaction
	}

	@Test
	public void testCreateAuthToken_expectOk() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken);
	}

	@Test
	public void testIsAuthTokenValid_expectTokenMustBeValidRightAfterCreation() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken);

		AuthToken result = authTokenService.isAuthTokenValid(user.getUuid(), authToken.getUuid(),
				authToken.getTokenValue());
		assertNotNull(result);
	}

	@Test
	public void testIsAuthTokenValid_expectWillNotUpdateLastVerifiedForOldValue() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");
		AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken);

		authTokenDao.updateToken(authToken.getUuid(), 5, null);

		authToken = authTokenService.getAuthTokenByUuid(authToken.getUuid());

		assertTrue(authToken.getLastVerifiedAt() > 5);
	}

	@Test
	public void testUpdateToken_expectValueWillBeUpdated() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		AuthToken authToken = authTokenService.createAuthToken(user.getEmail(), "LOCAL", "tUuid1", "tValue1");
		assertNotNull(authToken);
		authTokenDao.updateToken(authToken.getUuid(), new Date().getTime() + 1, "newValue2");

		authToken = authTokenService.getAuthTokenByUuid(authToken.getUuid());
		assertEquals("newValue2", authToken.getTokenValue());
	}

	@Test
	public void testDeleteAuthToken_expectDeletedAuthTokenMustNotBeValid() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		AuthToken authToken = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken);

		AuthToken result = authTokenService.isAuthTokenValid(user.getUuid(), authToken.getUuid(),
				authToken.getTokenValue());
		assertNotNull(result);

		authTokenService.deleteAuthToken(authToken.getUuid());
		result = authTokenService.isAuthTokenValid(user.getUuid(), authToken.getUuid(), authToken.getTokenValue());
		assertNull(result);
	}

	@Test
	public void testFindExpiredAuthTokens_expect2Tokens() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		AuthToken authToken1 = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken1);
		Thread.sleep(501);

		AuthToken authToken2 = authTokenService.authenticate(user.getEmail(), "aaa", "LOCAL");
		assertNotNull(authToken2);
		Thread.sleep(501);

		List<AuthToken> tokens = authTokenService.findUserAuthTokens(user.getUuid());
		assertEquals(2, tokens.size());
	}

	@Test
	public void testUpdateUserPassword_expectWillReportAffectedRecords() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		int result = passwordDao.updateUserPassword(user.getUuid(), "new-hash");
		assertTrue(result > 0);

		// do the same. Still expect affected > 0
		result = passwordDao.updateUserPassword(user.getUuid(), "new-hash");
		assertTrue(result > 0);
	}

	@Test
	public void testSetRestorationToken_expectWillReportAffectedRecords() throws Exception {
		User user = userService.createUser(UserFactory.createNewUserTemplate());
		passwordService.setUserPassword(user.getUuid(), "aaa");

		int result = passwordDao.setRestorationToken(user.getUuid(), "new-hash");
		assertTrue(result > 0);

		// do the same. Still expect affected > 0
		result = passwordDao.setRestorationToken(user.getUuid(), "new-hash");
		assertTrue(result > 0);
	}
}