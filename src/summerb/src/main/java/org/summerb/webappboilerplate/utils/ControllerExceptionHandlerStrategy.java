package org.summerb.webappboilerplate.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface ControllerExceptionHandlerStrategy {

	ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req, HttpServletResponse res);

}