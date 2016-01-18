package org.summerb.microservices.properties.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.microservices.properties.impl.dao.StringIdAliasDao;

public class StringIdAliasServiceEagerImplTest {

	@Test(expected = IllegalStateException.class)
	public void testInitialization_defensive_expectExceptionIfDaoIsNotSet() throws Exception {
		StringIdAliasServiceEagerImpl fixture = new StringIdAliasServiceEagerImpl();
		fixture.afterPropertiesSet();
	}

	@Test
	public void testInitialization_whitebox_expectAsyncAliasLoad() throws Exception {
		StringIdAliasServiceEagerImpl fixture = StringIdAliasServiceEagerImplFactory
				.createInstanceWithExecutorService();

		long now = System.currentTimeMillis();
		fixture.afterPropertiesSet();
		assertTrue(System.currentTimeMillis() - now < 40);
		// IMPORTANT!!! Downdeep in DAO there is a delay simulation for at least
		// 250ms
		long result = fixture.getAliasFor("str5");
		assertEquals(5, result);
		assertTrue(System.currentTimeMillis() - now > 200);
	}

	@Test
	public void testInitialization_whitebox_expectCorrectValueForPrecachedAlias() throws Exception {
		StringIdAliasServiceEagerImpl fixture = StringIdAliasServiceEagerImplFactory
				.createInstanceWithoutExecutorService();
		fixture.afterPropertiesSet();

		long result = fixture.getAliasFor("str5");
		assertEquals(5, result);
	}

	@Test(expected = RuntimeException.class)
	public void testLoadAllAliases_whitebox_expectExceptionOnException() {
		StringIdAliasServiceEagerImpl fixture = new StringIdAliasServiceEagerImpl();
		StringIdAliasDao stringIdAliasDao = Mockito.mock(StringIdAliasDao.class);
		when(stringIdAliasDao.loadAllAliases(any(PagerParams.class)))
				.thenThrow(new IllegalStateException("test exception"));

		fixture.loadAllAliases();
		fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetAliases_whitebox_expectExceptionOnException() {
		StringIdAliasServiceEagerImpl fixture = new StringIdAliasServiceEagerImpl();
		StringIdAliasDao stringIdAliasDao = Mockito.mock(StringIdAliasDao.class);
		when(stringIdAliasDao.loadAllAliases(any(PagerParams.class)))
				.thenThrow(new IllegalStateException("test exception"));

		fixture.getAliases();
		fail();
	}

}
