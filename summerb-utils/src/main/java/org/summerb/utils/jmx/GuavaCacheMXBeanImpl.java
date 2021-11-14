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
package org.summerb.utils.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

public class GuavaCacheMXBeanImpl<K, V> implements GuavaCacheMXBean, LoadingCache<K, V> {
	private static Logger log = LogManager.getLogger(GuavaCacheMXBeanImpl.class);

	private final LoadingCache<K, V> cache;

	public GuavaCacheMXBeanImpl(String cname, LoadingCache<K, V> cache) {
		this.cache = cache;
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			String name = String.format("%s:type=Cache,name=%s", cache.getClass().getPackage().getName(), cname);
			ObjectName mxBeanName = new ObjectName(name);
			if (!server.isRegistered(mxBeanName)) {
				server.registerMBean(this, new ObjectName(name));
			}
		} catch (Throwable t) {
			log.error("Failed to init jmx bean for cache " + cname, t);
		}
	}

	@Override
	public long getRequestCount() {
		return cache.stats().requestCount();
	}

	@Override
	public long getHitCount() {
		return cache.stats().hitCount();
	}

	@Override
	public double getHitRate() {
		return cache.stats().hitRate();
	}

	@Override
	public long getMissCount() {
		return cache.stats().missCount();
	}

	@Override
	public double getMissRate() {
		return cache.stats().missRate();
	}

	@Override
	public long getLoadCount() {
		return cache.stats().loadCount();
	}

	@Override
	public long getLoadSuccessCount() {
		return cache.stats().loadSuccessCount();
	}

	@Override
	public long getLoadExceptionCount() {
		return cache.stats().loadExceptionCount();
	}

	@Override
	public double getLoadExceptionRate() {
		return cache.stats().loadExceptionRate();
	}

	@Override
	public long getTotalLoadTime() {
		return cache.stats().totalLoadTime();
	}

	@Override
	public double getAverageLoadPenalty() {
		return cache.stats().averageLoadPenalty();
	}

	@Override
	public long getEvictionCount() {
		return cache.stats().evictionCount();
	}

	@Override
	public long getSize() {
		return cache.size();
	}

	@Override
	public void cleanUp() {
		cache.cleanUp();
	}

	@Override
	public void invalidateAll() {
		cache.invalidateAll();
	}

	@Override
	public V getIfPresent(Object key) {
		return cache.getIfPresent(key);
	}

	@Override
	public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
		return cache.get(key, valueLoader);
	}

	@Override
	public ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
		return cache.getAllPresent(keys);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		cache.putAll(m);
	}

	@Override
	public void invalidate(Object key) {
		cache.invalidate(key);
	}

	@Override
	public void invalidateAll(Iterable<?> keys) {
		cache.invalidateAll(keys);
	}

	@Override
	public long size() {
		return cache.size();
	}

	@Override
	public CacheStats stats() {
		return cache.stats();
	}

	@Override
	public V getUnchecked(K key) {
		return cache.getUnchecked(key);
	}

	@Override
	public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
		return cache.getAll(keys);
	}

	@SuppressWarnings("deprecation")
	@Override
	public V apply(K key) {
		return cache.apply(key);
	}

	@Override
	public void refresh(K key) {
		cache.refresh(key);
	}

	@Override
	public ConcurrentMap<K, V> asMap() {
		return cache.asMap();
	}

	@Override
	public V get(K key) throws ExecutionException {
		return cache.get(key);
	}
}
