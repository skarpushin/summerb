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
package org.summerb.webappboilerplate.utils;

import com.google.common.base.Preconditions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.summerb.security.api.AuditEvents;
import org.summerb.security.api.dto.ScalarValue;
import org.summerb.security.impl.AuditEventsDefaultImpl;

/**
 * Very naive impl that could be hacked by using selenium, but I hope it's sufficient for now
 *
 * @author sergeyk
 */
@Controller
public class CaptchaController implements InitializingBean {
  public static final String AUDIT_CAPTCHA_NOMATCH = "CPTCHNM";
  public static final String AUDIT_CAPTCHA_MISUSE = "CPTCHMU";
  public static final String AUDIT_CAPTCHA_INVALID = "CPTCHNVLD";

  protected AuditEvents auditEvents;
  protected static AuditEvents staticAuditLog = new AuditEventsDefaultImpl();

  @Override
  public void afterPropertiesSet() {
    if (auditEvents == null) {
      auditEvents = new AuditEventsDefaultImpl();
    }
    staticAuditLog = auditEvents;
  }

  @GetMapping("/rest/captcha-token")
  public @ResponseBody Map<String, String> getCaptcha(
      @RequestParam("goal") String goal, HttpServletRequest request) {
    Preconditions.checkArgument(StringUtils.hasText(goal));
    String token = findToken(goal, request);
    if (token == null) {
      auditEvents.report(AUDIT_CAPTCHA_MISUSE, null);
      throw new IllegalArgumentException("captcha misuse, it was never created");
    }

    return Collections.singletonMap("captcha", token);
  }

  protected static String findToken(String goal, HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }

    Object ret = session.getAttribute(buildCaptchaTokenAttrName(goal));
    if (ret == null) {
      return null;
    }

    return ret.toString();
  }

  public static void putToken(String goal, HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    Preconditions.checkState(session != null);
    session.setAttribute(buildCaptchaTokenAttrName(goal), UUID.randomUUID().toString());
  }

  public static void assertCaptchaTokenValid(
      String goal, String providedToken, HttpServletRequest request) {
    if (!StringUtils.hasText(providedToken)) {
      staticAuditLog.report(AUDIT_CAPTCHA_INVALID, ScalarValue.forV(providedToken));
      throw new IllegalArgumentException("Invalid captcha value");
    }

    String expectedToken = findToken(goal, request);
    if (expectedToken == null) {
      staticAuditLog.report(AUDIT_CAPTCHA_MISUSE, null);
      throw new IllegalArgumentException("captcha misuse, it was never created, cant compare");
    }

    if (!providedToken.equals(expectedToken)) {
      staticAuditLog.report(AUDIT_CAPTCHA_NOMATCH, ScalarValue.forV(providedToken));
      throw new IllegalArgumentException("Captcha doens match our records");
    }

    // reset token so that it cant be used multiple times
    HttpSession session = request.getSession(true);
    session.setAttribute(buildCaptchaTokenAttrName(goal), null);
  }

  protected static String buildCaptchaTokenAttrName(String goal) {
    return "captchaToken_" + goal;
  }

  public AuditEvents getAuditLog() {
    return auditEvents;
  }

  @Autowired(required = false)
  public void setAuditLog(AuditEvents auditEvents) {
    this.auditEvents = auditEvents;
  }
}
