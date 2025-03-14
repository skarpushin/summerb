/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.users.impl.dao.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.impl.dao.AuthTokenDao;
import org.summerb.utils.jmx.MapSizeMXBean;
import org.summerb.utils.jmx.MapSizeMXBeanImpl;

public class AuthTokenDaoInMemoryImpl implements AuthTokenDao, InitializingBean, DisposableBean {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected String pathNameToPersistedTokens;

  protected Map<String, AuthToken> tokens = new HashMap<>();
  protected Multimap<String, AuthToken> idxByUser = HashMultimap.create();
  protected MapSizeMXBean mxBean;

  @Override
  public synchronized void createAuthToken(AuthToken authToken) {
    AuthToken clonned = clone(authToken);
    internalAddToken(clonned);
  }

  protected void internalAddToken(AuthToken clonned) {
    tokens.put(clonned.getUuid(), clonned);
    idxByUser.put(clonned.getUserUuid(), clonned);
  }

  @Override
  public synchronized AuthToken findAuthTokenByUuid(String authTokenUuid) {
    AuthToken ret = tokens.get(authTokenUuid);
    return ret == null ? null : clone(ret);
  }

  @Override
  public synchronized void updateToken(String authTokenUuid, long now, String newTokenValue) {
    AuthToken token = tokens.get(authTokenUuid);
    if (token == null || token.getLastVerifiedAt() >= now) {
      return;
    }

    token.setLastVerifiedAt(now);
    token.setTokenValue(newTokenValue);
  }

  @Override
  public synchronized void deleteAuthToken(String authTokenUuid) {
    AuthToken token = tokens.remove(authTokenUuid);
    if (token == null) {
      return;
    }

    idxByUser.get(token.getUserUuid()).remove(token);
  }

  @Override
  public synchronized List<AuthToken> findAuthTokensByUser(String userUuid) {
    Collection<AuthToken> data = idxByUser.get(userUuid);
    List<AuthToken> ret = new ArrayList<>(data.size());
    for (AuthToken d : data) {
      ret.add(clone(d));
    }
    return ret;
  }

  protected AuthToken clone(AuthToken b) {
    AuthToken a = new AuthToken();
    a.setClientIp(b.getClientIp());
    a.setCreatedAt(b.getCreatedAt());
    a.setExpiresAt(b.getExpiresAt());
    a.setLastVerifiedAt(b.getLastVerifiedAt());
    a.setTokenValue(b.getTokenValue());
    a.setUserUuid(b.getUserUuid());
    a.setUuid(b.getUuid());
    return a;
  }

  @Override
  public synchronized void afterPropertiesSet() {
    mxBean = new MapSizeMXBeanImpl("AuthTokens", tokens);
    loadPersistedTokens();
  }

  protected void loadPersistedTokens() {
    try {
      if (pathNameToPersistedTokens == null) {
        log.info("No pathNameToPersistedTokens configured, no tokens will be loaded");
        return;
      }
      File file = new File(pathNameToPersistedTokens);
      if (!file.exists()) {
        log.warn(
            "pathNameToPersistedTokens configured, but file nto found, no tokens will be loaded");
        return;
      }

      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        long now = System.currentTimeMillis();
        log.info("Loaded tokens from: {}", file.getAbsolutePath());
        for (String line; (line = br.readLine()) != null; ) {
          AuthToken token = tryParseToken(line);
          if (token == null || token.getExpiresAt() < now) {
            continue;
          }
          internalAddToken(token);
        }
        log.info("Loaded tokens count: {}", tokens.size());
      }
    } catch (Throwable t) {
      log.error("Failed to load persisted tokens", t);
    }
  }

  protected AuthToken tryParseToken(String line) {
    try {
      if (!StringUtils.hasText(line) || line.indexOf('\t') < 0) {
        return null;
      }
      String[] parts = line.split("\t");
      if (parts.length != 7) {
        return null;
      }

      AuthToken ret = new AuthToken();
      ret.setClientIp(parts[0]);
      ret.setCreatedAt(Long.parseLong(parts[1]));
      ret.setExpiresAt(Long.parseLong(parts[2]));
      ret.setLastVerifiedAt(Long.parseLong(parts[3]));
      ret.setTokenValue(parts[4]);
      ret.setUserUuid(parts[5]);
      ret.setUuid(parts[6]);
      return ret;
    } catch (Throwable t) {
      log.warn("Failed to parse token from line: {}", line, t);
      return null;
    }
  }

  @Override
  public synchronized void destroy() {
    persistTokens();
  }

  protected void persistTokens() {
    try {
      if (pathNameToPersistedTokens == null) {
        log.info("No pathNameToPersistedTokens configured, no tokens will be persisted");
        return;
      }
      File file = new File(pathNameToPersistedTokens);
      Preconditions.checkState(
          !file.exists() || file.delete(),
          "Failed to ensure file is not exist before we start writing new one");

      try (PrintWriter output = new PrintWriter(new FileWriter(file, true))) {
        long now = System.currentTimeMillis();
        for (AuthToken token : tokens.values()) {
          if (token.getExpiresAt() < now) {
            continue;
          }

          output.printf("%s%s", formatToken(token), System.lineSeparator());
          output.flush();
        }
      }
      log.info("Tokens persisted: {}", file.getAbsolutePath());
    } catch (Throwable t) {
      log.error("Failed to persist tokens", t);
    }
  }

  protected String formatToken(AuthToken t) {
    String sb =
        t.getClientIp()
            + "\t"
            + t.getCreatedAt()
            + "\t"
            + t.getExpiresAt()
            + "\t"
            + t.getLastVerifiedAt()
            + "\t"
            + t.getTokenValue()
            + "\t"
            + t.getUserUuid()
            + "\t"
            + t.getUuid();
    return sb;
  }

  public String getPathNameToPersistedTokens() {
    return pathNameToPersistedTokens;
  }

  public void setPathNameToPersistedTokens(String pathNameToPersistedTokens) {
    this.pathNameToPersistedTokens = pathNameToPersistedTokens;
  }
}
