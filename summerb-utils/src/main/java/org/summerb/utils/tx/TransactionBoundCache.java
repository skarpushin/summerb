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
package org.summerb.utils.tx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.summerb.utils.jmx.GuavaCacheMXBeanImpl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;

/**
 * This impl represents transaction-bound cache. It ensure that cache changes
 * will respect transaction boundary. If transaction commit, then changes
 * propagated to global cache, otherwise all changes will affect
 * transaction-local cache only
 * 
 * @author sergeyk
 *
 * @param <K>
 * @param <V>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TransactionBoundCache<K, V> implements LoadingCache<K, V> {
	private static Logger log = LogManager.getLogger(TransactionBoundCache.class);

	private String cacheName;
	private CacheLoader<K, V> loader;

	private LoadingCache<K, V> actual;

	// NOTE: It's not static because we need it to be bound to specific cache,
	// not to singleton
	private final ThreadLocal<TransactionBoundCacheEntry> transactionBoundCacheEntries = new ThreadLocal<TransactionBoundCacheEntry>();

	/**
	 * This marker will be used as a value for {@link #transactionBoundCacheEntries}
	 * to prevent from sub-instantiating local cache. if this marker found in thread
	 * local then global cache will be returned. This is a fix for defect found
	 * while testing "MSCAR-9 Article A will be evicted from cache if it depends on
	 * Article B and Article B was just updated "
	 */
	private final TransactionBoundCacheEntry markerUseGlobalCache = new TransactionBoundCacheEntry<>();

	public TransactionBoundCache(String cacheName, CacheBuilder<K, V> cacheBuilder, CacheLoader<K, V> loader) {
		this.loader = loader;
		this.cacheName = cacheName;
		// NOTE: We're not tracking removals from global cache when working with
		// local. But since transaction are not that long - probability of
		// race condition is relatively low
		// RACECONDITION possible - if entry was removed from global, but we
		// still have it locally in transaction
		actual = cacheBuilder.build(loader);
		actual = new GuavaCacheMXBeanImpl<>(cacheName, actual);
	}

	/**
	 * Get for write purpose
	 */
	private LoadingCache<K, V> get() {
		TransactionBoundCacheEntry transactionBoundCacheEntry = transactionBoundCacheEntries.get();
		if (transactionBoundCacheEntry == markerUseGlobalCache) {
			if (log.isTraceEnabled()) {
				log.trace(cacheName + ": Transaction is committing. Using global cache");
			}
			return actual;
		}

		String curTranName = TransactionSynchronizationManager.getCurrentTransactionName();
		if (transactionBoundCacheEntry != null) {
			if (log.isTraceEnabled()) {
				log.trace(cacheName + ": Using transaction-local (assumming active transaction) cache tran="
						+ curTranName);
			}
			return transactionBoundCacheEntry.transactionBound;
		}

		boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
		// NOTE: This will return true even at the end of committing transaction. So
		// that's why we rely on #markerUseGlobalCache
		if (!synchronizationActive) {
			if (log.isTraceEnabled()) {
				log.trace(cacheName + ": Using global cache");
			}
			return actual;
		}

		if (log.isTraceEnabled()) {
			log.trace(cacheName + ": Constructing transaction-local cache, tran=" + curTranName);
		}

		TransactionSynchronizationManager.registerSynchronization(synchronization);
		TransactionBoundCacheEntry newCache = new TransactionBoundCacheEntry<K, V>();
		newCache.transactionBound = CacheBuilder.newBuilder().removalListener(localRemovalListener).build(loader);
		// TBD: Instead of copy-on-write the whole thing, isn't it better to
		// implement inheritance? Like if we didn't find it in this cache, then
		// lookup parent?
		newCache.transactionBound.putAll(actual.asMap());
		newCache.transactionBoundRemovals = new HashSet<K>();
		transactionBoundCacheEntries.set(newCache);
		return newCache.transactionBound;
	}

	private TransactionSynchronization synchronization = new TransactionSynchronizationAdapter() {
		@Override
		public void afterCommit() {
			TransactionBoundCacheEntry transactionBoundCacheEntry = transactionBoundCacheEntries.get();
			if (transactionBoundCacheEntry == null || transactionBoundCacheEntry == markerUseGlobalCache) {
				log.warn(cacheName + ": Inconsistent state. If we're here - there must be linked threadLocal");
				return;
			}

			Set invalidatedKeys = transactionBoundCacheEntry.transactionBoundRemovals;
			if (log.isTraceEnabled()) {
				log.trace(cacheName
						+ ": Clean-up after transaction commit. Removing invalidated objects from global cache: "
						+ Arrays.toString(invalidatedKeys.toArray()));
			}

			transactionBoundCacheEntries.set(markerUseGlobalCache);
			if (!invalidatedKeys.isEmpty()) {
				actual.invalidateAll(invalidatedKeys);
			}
		};

		@Override
		public void afterCompletion(int status) {
			transactionBoundCacheEntries.remove();
			if (log.isTraceEnabled()) {
				String result = status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK";
				log.trace(cacheName + ": Transaction-bound cache cleaned-up, status = " + result);
			}
		}
	};

	private RemovalListener<? super K, ? super V> localRemovalListener = new RemovalListener<K, V>() {
		@Override
		public void onRemoval(RemovalNotification<K, V> notification) {
			TransactionBoundCacheEntry transactionBoundCacheEntry = transactionBoundCacheEntries.get();
			if (transactionBoundCacheEntry == null) {
				log.error(cacheName + ": Weird. We receive removal event, but no transaction-bound cache exists");
				return;
			}
			if (transactionBoundCacheEntry == markerUseGlobalCache) {
				log.error(cacheName + ": Weird. We received removal event, while trasaction is completting");
				return;
			}

			if (log.isTraceEnabled()) {
				log.trace(cacheName + ": Element is removed from local cache: " + notification.getKey());
			}

			transactionBoundCacheEntry.transactionBoundRemovals.add(notification.getKey());
		}
	};

	@Override
	public V getIfPresent(Object key) {
		return get().getIfPresent(key);
	}

	@Override
	public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
		return get().get(key, valueLoader);
	}

	@Override
	public ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
		return get().getAllPresent(keys);
	}

	@Override
	public void put(K key, V value) {
		get().put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		get().putAll(m);
	}

	@Override
	public void invalidate(Object key) {
		get().invalidate(key);
	}

	@Override
	public void invalidateAll(Iterable<?> keys) {
		get().invalidateAll(keys);
	}

	@Override
	public void invalidateAll() {
		get().invalidateAll();
	}

	@Override
	public long size() {
		return get().size();
	}

	@Override
	public CacheStats stats() {
		return actual.stats();
	}

	@Override
	public void cleanUp() {
		get().cleanUp();
	}

	@Override
	public V get(K key) throws ExecutionException {
		return get().get(key);
	}

	@Override
	public V getUnchecked(K key) {
		return get().getUnchecked(key);
	}

	@Override
	public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
		return get().getAll(keys);
	}

	@SuppressWarnings("deprecation")
	@Override
	public V apply(K key) {
		return get().apply(key);
	}

	@Override
	public void refresh(K key) {
		get().refresh(key);
	}

	@Override
	public ConcurrentMap<K, V> asMap() {
		return get().asMap();
	}

	private static class TransactionBoundCacheEntry<K, V> {
		LoadingCache<K, V> transactionBound;
		Set<K> transactionBoundRemovals;
	}

}
