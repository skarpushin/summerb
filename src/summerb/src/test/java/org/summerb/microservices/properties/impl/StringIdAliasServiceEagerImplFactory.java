package org.summerb.microservices.properties.impl;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.microservices.properties.impl.dao.AliasEntry;
import org.summerb.microservices.properties.impl.dao.StringIdAliasDao;

public class StringIdAliasServiceEagerImplFactory {
	public static final String NAME = "NAME";
	public static final Long NAME_ALIAS = 1000L;

	public static StringIdAliasServiceEagerImpl createInstanceWithExecutorService() {
		StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl();
		ret.setExecutorService(Executors.newSingleThreadExecutor());

		ret.setStringIdAliasDao(createDaoMock());

		return ret;
	}

	public static StringIdAliasServiceEagerImpl createInstanceWithoutExecutorService() {
		StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl();

		ret.setStringIdAliasDao(createDaoMock());

		return ret;
	}

	private static StringIdAliasDao createDaoMock() {
		StringIdAliasDao ret = Mockito.mock(StringIdAliasDao.class);

		when(ret.createAliasFor(NAME)).thenReturn(NAME_ALIAS);
		when(ret.findAliasFor(NAME)).thenReturn(NAME_ALIAS);

		when(ret.loadAllAliases(any(PagerParams.class))).thenAnswer(new Answer<PaginatedList<Entry<String, Long>>>() {
			@Override
			public PaginatedList<Entry<String, Long>> answer(InvocationOnMock invocation) throws Throwable {
				PagerParams pagerParams = (PagerParams) invocation.getArguments()[0];

				// Synthetic pause, simulate
				Thread.sleep(250);

				PaginatedList<Entry<String, Long>> result = new PaginatedList<Entry<String, Long>>();
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
