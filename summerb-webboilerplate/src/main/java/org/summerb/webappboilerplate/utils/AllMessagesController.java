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
package org.summerb.webappboilerplate.utils;

import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.summerb.utils.spring.AllMessagesProvider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;

/**
 * This controller exposes all messages as a JSON object (map)
 *
 * @author sergeyk
 */
@Controller
public class AllMessagesController implements InitializingBean {
  protected static final long MILLIS_PER_DAY = 86400000;

  @Autowired protected AllMessagesProvider allMessagesProvider;

  protected LoadingCache<Locale, Properties> messagesCache;
  protected Gson gson;

  // TBD: Do not hardcode this, check when messages were actually reloaded
  protected long lastModified = System.currentTimeMillis();

  @Override
  public void afterPropertiesSet() throws Exception {
    messagesCache =
        CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(allMessagesProvider.getReloadIntervalSeconds(), TimeUnit.SECONDS)
            .recordStats()
            .build(messagesLoader);

    gson = new Gson();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/rest/msgs")
  public @ResponseBody Properties getMessageBundle() throws ExecutionException {
    return messagesCache.get(CurrentRequestUtils.getLocale());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/msgs.js")
  public ResponseEntity<String> getMessageBundleJs(WebRequest request, HttpServletResponse response)
      throws ExecutionException {
    // see if not modified
    if (request.checkNotModified(lastModified)) {
      return new ResponseEntity<String>(HttpStatus.NOT_MODIFIED);
    }

    // set headers
    HttpHeaders responseHeaders = new HttpHeaders();
    response.setHeader("Cache-Control", "max-age: 84600");
    responseHeaders.setExpires(System.currentTimeMillis() + MILLIS_PER_DAY);
    responseHeaders.set("Content-Type", "text/javascript; charset=UTF-8");
    // NOTE: Looks like there is a bug in the spring - it will add
    // Last-Modified twice to the response
    // responseHeaders.setLastModified(maxLastModified);
    response.setDateHeader("Last-Modified", lastModified);
    Properties msgs = messagesCache.get(CurrentRequestUtils.getLocale());
    return new ResponseEntity<String>(
        "var msgs = " + gson.toJson(msgs), responseHeaders, HttpStatus.OK);
  }

  protected CacheLoader<Locale, Properties> messagesLoader =
      new CacheLoader<Locale, Properties>() {
        @Override
        public Properties load(Locale key) throws Exception {
          return allMessagesProvider.getAllMessages(key);
        }
      };
}
