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
package org.summerb.webappboilerplate.security.dto;

public class PasswordChange {
	public static final String FN_NEW_PASSWORD_AGAIN = "newPasswordAgain";
	public static final String FN_CURRENT_PASSWORD = "currentPassword";

	private String currentPassword;
	/**
	 * IMPORTANT: This is a little hack, but thi field is intentionally called
	 * password, but not newPassword, in order to avoid the need of
	 * FieldValidationException mapping comed from User service
	 */
	private String password;
	private String newPasswordAgain;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String newPassword) {
		this.currentPassword = newPassword;
	}

	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}

	public void setNewPasswordAgain(String newPasswordAgain) {
		this.newPasswordAgain = newPasswordAgain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
}
