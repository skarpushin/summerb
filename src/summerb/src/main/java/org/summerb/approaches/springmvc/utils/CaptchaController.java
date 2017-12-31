package org.summerb.approaches.springmvc.utils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.summerb.approaches.security.api.AuditLog;
import org.summerb.approaches.security.api.dto.ScalarValue;
import org.summerb.approaches.security.impl.AuditLogDefaultImpl;

import com.google.common.base.Preconditions;

/**
 * Very naive impl that could be hacked by using selenium, but I hope it's
 * sufficient for now
 * 
 * @author sergeyk
 *
 */
@Controller
public class CaptchaController implements InitializingBean {
	public static final String AUDIT_CAPTCHA_NOMATCH = "CPTCHNM";
	public static final String AUDIT_CAPTCHA_MISUSE = "CPTCHMU";
	public static final String AUDIT_CAPTCHA_INVALID = "CPTCHNVLD";

	private AuditLog auditLog;
	private static AuditLog staticAuditLog = new AuditLogDefaultImpl();

	@Override
	public void afterPropertiesSet() throws Exception {
		if (auditLog == null) {
			auditLog = new AuditLogDefaultImpl();
		}
		staticAuditLog = auditLog;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rest/captcha-token")
	public @ResponseBody Map<String, String> getCaptcha(@RequestParam("goal") String goal, HttpServletRequest request) {
		Preconditions.checkArgument(StringUtils.hasText(goal));
		String token = findToken(goal, request);
		if (token == null) {
			auditLog.report(AUDIT_CAPTCHA_MISUSE, null);
			throw new IllegalArgumentException("captcha misuse, it was never created");
		}

		return Collections.singletonMap("captcha", token);
	}

	private static String findToken(String goal, HttpServletRequest request) {
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

	public static void assertCaptchaTokenValid(String goal, String providedToken, HttpServletRequest request) {
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

	private static String buildCaptchaTokenAttrName(String goal) {
		return "captchaToken_" + goal;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	@Autowired(required = false)
	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

}
