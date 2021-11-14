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

import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.properties.api.exceptions.PropertyServiceUnexpectedException;
import org.summerb.properties.impl.dao.AliasEntry;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.properties.internal.StringIdAliasService;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Impl which holds all aliases in-memory. So it performs extremely fast. But it
 * will go to the database when it will need to create new alias.
 * 
 * If it will hit duplicate key exception, it will load this alias, so it's safe
 * to use this impl in webfarm configuration
 * 
 * @author skarpushin
 * 
 */
public class StringIdAliasServiceEagerImpl implements StringIdAliasService, InitializingBean {
	private static final int EAGER_LOAD_BATCH_SIZE = 100;
	private static Logger log = LogManager.getLogger(StringIdAliasServiceEagerImpl.class);

	private StringIdAliasDao stringIdAliasDao;
	private ExecutorService executorService;

	private BiMap<String, Long> aliases;
	private Future<BiMap<String, Long>> aliasesFuture;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(stringIdAliasDao != null);

		if (executorService == null) {
			log.warn("executorService is not set. Will create own one");
			executorService = Executors.newSingleThreadExecutor();
		}

		// Schedule background task for retrieving map aliases map
		aliasesFuture = executorService.submit(aliasesBackgroundResolver);
	}

	protected final Callable<BiMap<String, Long>> aliasesBackgroundResolver = new Callable<BiMap<String, Long>>() {
		@Override
		public BiMap<String, Long> call() {
			return loadAllAliases();
		}
	};

	protected final BiMap<String, Long> loadAllAliases() {
		try {
			BiMap<String, Long> ret = HashBiMap.create();
			long maxOffset = -2;
			for (long offset = 0; maxOffset == -2 || offset < maxOffset; offset = offset + EAGER_LOAD_BATCH_SIZE) {
				PagerParams pagerParams = new PagerParams(offset, EAGER_LOAD_BATCH_SIZE);
				PaginatedList<AliasEntry> loadedAliases = stringIdAliasDao.loadAllAliases(pagerParams);
				maxOffset = loadedAliases.getTotalResults() - 1;

				for (Entry<String, Long> entry : loadedAliases.getItems()) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
			return ret;
		} catch (Throwable t) {
			String msg = "Failed to eagerly load alias map";
			log.error(msg, t);
			throw new PropertyServiceUnexpectedException(msg, t);
		}
	}

	protected final BiMap<String, Long> getAliases() {
		if (aliases == null) {
			try {
				aliases = aliasesFuture.get();
			} catch (Throwable e) {
				throw new PropertyServiceUnexpectedException("Failed to cache alias map", e);
			}
		}

		return aliases;
	}

	@Override
	public long getAliasFor(String str) {
		Long ret = getAliases().get(str);
		if (ret == null) {
			return registerAlias(str);
		}
		return ret.longValue();
	}

	/**
	 * Will register alias (or retrieve already stored, only - useful for multi
	 * server installation - environment - AKA webfarm)
	 */
	protected final synchronized long registerAlias(String str) {
		// WARNING: This method must be called only after getALiasFor invoked at
		// least once, otherwise NullPointerException might happen
		Long ret = aliases.get(str);

		// sanity check if someone has already stored it
		if (ret != null) {
			return ret.longValue();
		}

		long alias;
		try {
			try {
				alias = doCreateAlias(str);
			} catch (DuplicateKeyException dke) {
				log.debug("Looks like alias already exist in database, will load it");
				ret = stringIdAliasDao.findAliasFor(str);
				if (ret == null) {
					throw new PropertyServiceUnexpectedException(
							"Failed to create alias because of duplicate, but later was unable to find that duplicate.");
				}
				alias = ret.longValue();
			}
		} catch (Throwable t) {
			throw new PropertyServiceUnexpectedException("Failed to store alias", t);
		}

		// store it locally
		aliases.put(str, alias);

		// ret
		return alias;
	}

	/**
	 * This method will make use os separate thread in order to have separate
	 * transaction manager. We need this to comi our change even if parent
	 * transaction fails. This might sound strange but we assume it's ok for aliases
	 * to remain in the database (thase are not values)
	 * 
	 * @param str
	 * @return
	 */
	protected long doCreateAlias(String str) throws Exception {
		try {
			CreateAliasTask task = new CreateAliasTask(str);
			Future<Long> future = executorService.submit(task);
			return future.get();
		} catch (ExecutionException exc) {
			Throwables.throwIfInstanceOf(exc.getCause(), DuplicateKeyException.class);
			Throwables.propagateIfPossible(exc.getCause());

			throw new RuntimeException("Unexpectedly failed to create alias in separate thread", exc);
		} catch (Throwable t) {
			throw new RuntimeException("Unexpectedly failed to create alias in separate thread", t);
		}
	}

	private class CreateAliasTask implements Callable<Long> {
		String name;

		public CreateAliasTask(String name) {
			this.name = name;
		}

		@Override
		public Long call() throws Exception {
			return stringIdAliasDao.createAliasFor(name);
		}
	}

	@Override
	public String getNameByAlias(long alias) {
		return getAliases().inverse().get(alias);
	}

	public StringIdAliasDao getStringIdAliasDao() {
		return stringIdAliasDao;
	}

	public void setStringIdAliasDao(StringIdAliasDao stringIdAliasDao) {
		this.stringIdAliasDao = stringIdAliasDao;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

}
