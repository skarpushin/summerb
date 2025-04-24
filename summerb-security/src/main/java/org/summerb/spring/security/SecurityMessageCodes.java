/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.spring.security;

/**
 * List of message codes used on server side
 *
 * @author skarpushin
 */
public class SecurityMessageCodes {
  public static final String FAILED_TO_ACTIVATE_REGISTRATION =
      "security.failedToActivateRegistration";
  public static final String ALREADY_ACTIVATED = "security.alreadyActivated";
  public static final String NEED_ACTIVATION_TOKEN = "security.needActivationToken";
  public static final String REGISTRATION_ACTIVATED_OK = "security.registrationActivatedOk";
  public static final String REGISTRATION_ALREADY_STARTED = "security.registrationAlreadyStarted";
  public static final String AUTH_FATAL = "security.authFatal";
  public static final String CANT_LOGIN_UNTIL_REGISTRATION_ACTIVATED =
      "security.activationRequired";
  public static final String ACCOUNT_IS_NOT_CREATED_PLEASE_REGISTER =
      "security.registrationRequired";
  public static final String FAILED_TO_SEND_REGISTRATION_EMAIL =
      "security.failedToSendRegistrationEmail";
  public static final String FAILED_TO_SEND_PASSWORD_REST_EMAIL =
      "security.failedToSendPasswordResetEmail";
  public static final String INVALID_PASSWORD_RESET_TOKEN = "security.resetPasswordTokenInvalid";
  public static final String ACCOUNT_PASSWORD_RESET_OK = "security.passwordResetOk";
  public static final String VALIDATION_PASSWORDS_DO_NOT_MATCH =
      "security.secondPasswordDoesNotMatchFirst";
  public static final String BAD_CREDENTIALS = "security.badCredentials";

  public static final String LOGIN_REQUIRED = "security.login.required";
  public static final String ACCESS_DENIED = "security.accessDenied";
  public static final String ANONYMOUS = "security.anonymousUser";
  public static final String INVALID_SESSION = "security.invalidSession";

  public static final String NEW_PASSWORD = "term.newPassword";
  public static final String NEW_PASSWORD_AGAIN = "term.newPasswordAgain";
}
