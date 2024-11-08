/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.properties.api.exceptions.PropertyServiceUnexpectedException;
import org.summerb.properties.impl.dao.AliasEntry;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.properties.internal.StringIdAliasServiceVisibleForTesting;

public class StringIdAliasServiceCachedImpl
    implements StringIdAliasService, StringIdAliasServiceVisibleForTesting {
  private final Logger log = LoggerFactory.getLogger(getClass());

  protected StringIdAliasDao stringIdAliasDao;

  protected final Object syncRoot = new Object();
  protected volatile BiMap<String, Long> cache;

  public StringIdAliasServiceCachedImpl(StringIdAliasDao stringIdAliasDao) {
    Preconditions.checkNotNull(stringIdAliasDao, "stringIdAliasDao required");
    this.stringIdAliasDao = stringIdAliasDao;
  }

  protected BiMap<String, Long> getCache() {
    if (cache == null) {
      synchronized (syncRoot) {
        if (cache != null) {
          return cache;
        }

        log.debug("Loading all aliases from DB... {}", stringIdAliasDao);
        cache = loadAllAliases();
        log.debug("...done. Loaded count: {}", cache.size());
      }
    }
    return cache;
  }

  protected BiMap<String, Long> loadAllAliases() {
    try {
      BiMap<String, Long> ret = HashBiMap.create();
      List<AliasEntry> allAliases = stringIdAliasDao.loadAllAliases();
      allAliases.forEach(x -> ret.put(x.getKey(), x.getValue()));
      return ret;
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to load aliases", t);
    }
  }

  @Override
  public long getAliasFor(String str) {
    Long ret = getCache().get(str);
    if (ret == null) {
      ret = addAlias(str);
    }
    return ret;
  }

  protected long addAlias(String str) {
    try {
      synchronized (syncRoot) {
        Long ret = cache.get(str);
        if (ret != null) {
          return ret;
        }

        try {
          ret = stringIdAliasDao.createAliasFor(str);
          addNewMapping(str, ret);
          return ret;
        } catch (DuplicateKeyException dke) {
          ret = stringIdAliasDao.findAliasFor(str);
          if (ret == null) {
            throw new PropertyServiceUnexpectedException(
                "Failed to create alias because of duplicate, but later was unable to find that duplicate for key "
                    + str,
                dke);
          }
          addNewMapping(str, ret);
          return ret;
        }
      }
    } catch (Exception e) {
      throw new PropertyServiceUnexpectedException("Failed to add alias: '" + str + "'" + str, e);
    }
  }

  protected void addNewMapping(String str, Long ret) {
    HashBiMap<String, Long> newMap = HashBiMap.create(cache);
    newMap.put(str, ret);
    cache = newMap;
  }

  @Override
  public String getNameByAlias(long alias) {
    return getCache().inverse().get(alias);
  }

  /** This is needed for testing purposes */
  @Override
  public void clearCache() {
    synchronized (syncRoot) {
      cache = null;
    }
  }
}
