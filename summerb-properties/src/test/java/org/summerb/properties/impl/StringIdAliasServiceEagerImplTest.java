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
package org.summerb.properties.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.properties.impl.dao.StringIdAliasDao;

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
