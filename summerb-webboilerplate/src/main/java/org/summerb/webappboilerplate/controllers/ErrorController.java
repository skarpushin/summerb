/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.webappboilerplate.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.summerb.webappboilerplate.Views;

@Controller
public class ErrorController extends ControllerBase {
	@RequestMapping(method = RequestMethod.GET, value = "/error/unexpected")
	public String handleUnexpected(Model model, HttpServletRequest request) {
		Object exc = request.getAttribute("javax.servlet.error.exception");
		if (exc != null && exc instanceof Throwable) {
			log.warn("Horribly unexpected exception", (Throwable) exc);
		}
		// TBD: Output user-friendly (usefull) error message
		return Views.ERROR_UNEXPECTED;// ClassUtils.getShortName(ex.getClass());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/error/error404")
	public String handle404(Model model, HttpServletRequest request) {
		return Views.ERROR_404;
	}
}
