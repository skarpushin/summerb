/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.exceptions.ExceptionUtils;

public class StringIdAliasServiceEagerImplTest {

  @Test
  public void testInitialization_defensive_expectExceptionIfDaoIsNotSet() {
    assertThrows(IllegalArgumentException.class, () -> new StringIdAliasServiceEagerImpl(null));
  }

  @Test
  public void testInitialization_whitebox_expectAsyncAliasLoad() throws Exception {
    StringIdAliasServiceEagerImpl fixture =
        StringIdAliasServiceEagerImplFactory.createInstanceWithExecutorService();

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
    StringIdAliasServiceEagerImpl fixture =
        StringIdAliasServiceEagerImplFactory.createInstanceWithoutExecutorService();
    fixture.afterPropertiesSet();

    long result = fixture.getAliasFor("str5");
    assertEquals(5, result);
  }

  @Test
  public void testLoadAllAliases_whitebox_expectExceptionOnException() {
    StringIdAliasDao stringIdAliasDao = Mockito.mock(StringIdAliasDao.class);
    doThrow(new IllegalStateException("INTENTIONAL FAILURE FOR TEST PURPOSES"))
        .when(stringIdAliasDao)
        .loadAliasesPaged(any(PagerParams.class));

    StringIdAliasServiceEagerImpl fixture = new StringIdAliasServiceEagerImpl(stringIdAliasDao);

    RuntimeException ex = assertThrows(RuntimeException.class, fixture::loadAllAliases);
    IllegalStateException ise = ExceptionUtils.findExceptionOfType(ex, IllegalStateException.class);
    assertNotNull(ise);
    assertEquals("INTENTIONAL FAILURE FOR TEST PURPOSES", ise.getMessage());
  }

  @Test
  public void testGetAliases_whitebox_expectExceptionOnException() {
    StringIdAliasDao stringIdAliasDao = Mockito.mock(StringIdAliasDao.class);
    when(stringIdAliasDao.loadAliasesPaged(any(PagerParams.class)))
        .thenThrow(new IllegalStateException("test exception"));

    StringIdAliasServiceEagerImpl fixture = new StringIdAliasServiceEagerImpl(stringIdAliasDao);

    assertThrows(RuntimeException.class, fixture::getAliases);
  }
}
