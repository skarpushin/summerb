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

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.summerb.properties.impl.dao.AliasEntry;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

public class StringIdAliasServiceEagerImplFactory {
  public static final String NAME = "NAME";
  public static final Long NAME_ALIAS = 1000L;

  public static StringIdAliasServiceEagerImpl createInstanceWithExecutorService() {
    StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl(createDaoMock());
    ret.setExecutorService(Executors.newSingleThreadExecutor());
    return ret;
  }

  public static StringIdAliasServiceEagerImpl createInstanceWithoutExecutorService() {
    return new StringIdAliasServiceEagerImpl(createDaoMock());
  }

  protected static StringIdAliasDao createDaoMock() {
    StringIdAliasDao ret = Mockito.mock(StringIdAliasDao.class);

    when(ret.createAliasFor(NAME)).thenReturn(NAME_ALIAS);
    when(ret.findAliasFor(NAME)).thenReturn(NAME_ALIAS);

    when(ret.loadAliasesPaged(any(PagerParams.class)))
        .thenAnswer(
            new Answer<PaginatedList<Entry<String, Long>>>() {
              @Override
              public PaginatedList<Entry<String, Long>> answer(InvocationOnMock invocation)
                  throws Throwable {
                PagerParams pagerParams = (PagerParams) invocation.getArguments()[0];

                // Synthetic pause, simulate
                Thread.sleep(250);

                PaginatedList<Entry<String, Long>> result =
                    new PaginatedList<Entry<String, Long>>();
                result.setPagerParams(pagerParams);
                result.setTotalResults(150);
                ArrayList<Entry<String, Long>> items = new ArrayList<Entry<String, Long>>();
                result.setItems(items);
                long offset = pagerParams.getOffset();
                long max = -1;
                if (offset == 0) {
                  max = 100;
                } else if (offset == 100) {
                  max = 50;
                } else {
                  fail();
                }

                for (long i = offset; i < max; i++) {
                  items.add(new AliasEntry("str" + i, i));
                }

                return result;
              }
            });

    return ret;
  }
}
