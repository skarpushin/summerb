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
package org.summerb.webappboilerplate.security.dto;

import org.summerb.utils.DtoBase;

public class LoginParams implements DtoBase {
	private static final long serialVersionUID = 5895187972995172560L;
	public static final String FN_EMAIL = "email";
	public static final String FN_PASSWORD = "password";
	public static final String HEADER_REMEMBER_ME = "rememberMe";

	private String email;
	private String password;

	public LoginParams() {
	}

	public LoginParams(String login, String password) {
		this.email = login;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String login) {
		this.email = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
