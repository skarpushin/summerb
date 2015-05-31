package org.summerb.springvmc.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.summerb.springvmc.Views;

@Controller
@RequestMapping(value = "/error")
public class ErrorController extends ControllerBase {
	@RequestMapping(method = RequestMethod.GET, value = "unexpected")
	public String handleUnexpected(Model model, HttpServletRequest request) {
		return Views.ERROR_UNEXPECTED;
	}
}
