package org.summerb.approaches.springmvc.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.summerb.approaches.springmvc.Views;

@Controller
public class ErrorController extends ControllerBase {
	@RequestMapping(method = RequestMethod.GET, value = "/error/unexpected")
	public String handleUnexpected(Model model, HttpServletRequest request) {
		// TODO: Output user-friendly (usefull) error message
		return Views.ERROR_UNEXPECTED;// ClassUtils.getShortName(ex.getClass());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/error/error404")
	public String handle404(Model model, HttpServletRequest request) {
		return Views.ERROR_404;
	}
}
