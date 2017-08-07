package org.summerb.approaches.springmvc.utils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Preconditions;

/**
 * Very naive impl that could be hacked by using selenium, but I hope it's
 * sufficient for now
 * 
 * @author sergeyk
 *
 */
@Controller
public class CaptchaController {
	@RequestMapping(method = RequestMethod.GET, value = "/rest/captcha-token")
	public @ResponseBody Map<String, String> getCaptcha(@RequestParam("goal") String goal, HttpServletRequest request) {
		Preconditions.checkArgument(StringUtils.hasText(goal));
		String token = findToken(goal, request);
		return Collections.singletonMap("captcha", token);
	}

	private static String findToken(String goal, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Preconditions.checkState(session != null);

		Object ret = session.getAttribute(buildCaptchaTokenAttrName(goal));
		Preconditions.checkState(ret != null);

		return ret.toString();
	}

	public static void putToken(String goal, HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Preconditions.checkState(session != null);
		session.setAttribute(buildCaptchaTokenAttrName(goal), UUID.randomUUID().toString());
	}

	public static void assertCaptchaTokenValid(String goal, String value, HttpServletRequest request) {
		Preconditions.checkArgument(StringUtils.hasText(value), "Invalid captcha value");
		Preconditions.checkArgument(value.equals(findToken(goal, request)), "Incorrect captcha");
		// reset token so that it cant be used multiple times
		HttpSession session = request.getSession(true);
		session.setAttribute(buildCaptchaTokenAttrName(goal), null);
	}

	private static String buildCaptchaTokenAttrName(String goal) {
		return "captchaToken_" + goal;
	}

}
