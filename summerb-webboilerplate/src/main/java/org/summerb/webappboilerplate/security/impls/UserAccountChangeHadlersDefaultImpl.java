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
package org.summerb.webappboilerplate.security.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.email.api.EmailSender;
import org.summerb.email.api.dto.EmailMessage;
import org.summerb.email.api.dto.EmailTemplateParams;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.users.api.dto.User;
import org.summerb.utils.exceptions.GenericRuntimeException;
import org.summerb.webappboilerplate.security.apis.PasswordResetArmedHandler;
import org.summerb.webappboilerplate.security.apis.SecurityActionsUrlsProvider;
import org.summerb.webappboilerplate.security.apis.SecurityMailsMessageBuilderFactory;
import org.summerb.webappboilerplate.security.apis.UserRegisteredHandler;
import org.summerb.webappboilerplate.utils.AbsoluteUrlBuilder;

public class UserAccountChangeHadlersDefaultImpl
    implements UserRegisteredHandler, PasswordResetArmedHandler {
  public static final String ATTR_PASSWORD_RESET_LINK = "resetPasswordLink";
  public static final String ATTR_ACTIVATION_LINK = "activationLink";

  @Autowired private EmailSender emailSender;
  @Autowired private SecurityMailsMessageBuilderFactory securityMailsMessageBuilderFactory;
  @Autowired private SecurityActionsUrlsProvider securityActionsUrlsProvider;
  @Autowired private AbsoluteUrlBuilder absoluteUrlBuilder;

  private boolean sendRegistrationConfirmationRequest = true;
  private boolean sendPasswordResetRequest = true;

  @Override
  public void onUserRegistered(User user) {
    try {
      if (user.getEmail().contains("@throw")) {
        throw new IllegalStateException("test throw on email failure - emulating no email sent");
      }
      if (!sendRegistrationConfirmationRequest) {
        return;
      }

      String activationAbsoluteLink =
          absoluteUrlBuilder.buildExternalUrl(
              securityActionsUrlsProvider.buildRegistrationActivationPath(
                  user, buildActivationToken(user)));

      String senderEmail =
          securityMailsMessageBuilderFactory.getAccountOperationsSender().getEmail();
      EmailTemplateParams emailTemplateParams =
          new EmailTemplateParams(senderEmail, user, new Object());
      emailTemplateParams.getExtension().put(ATTR_ACTIVATION_LINK, activationAbsoluteLink);
      EmailMessage emailMessage =
          securityMailsMessageBuilderFactory
              .getRegistrationEmailBuilder()
              .buildEmail(senderEmail, user.getEmail(), emailTemplateParams);
      emailSender.sendEmail(emailMessage);
    } catch (Throwable t) {
      throw new GenericRuntimeException(SecurityMessageCodes.FAILED_TO_SEND_REGISTRATION_EMAIL, t);
    }
  }

  protected String buildActivationToken(User user) {
    // meant to be sub-classed
    return "";
  }

  @Override
  public void onPasswordResetRequested(User user, String passwordResetToken) {
    try {
      if (!sendPasswordResetRequest) {
        return;
      }

      String senderEmail =
          securityMailsMessageBuilderFactory.getAccountOperationsSender().getEmail();
      EmailTemplateParams emailTemplateParams =
          new EmailTemplateParams(senderEmail, user, new Object());
      String passwordResetAbsoluteLink =
          absoluteUrlBuilder.buildExternalUrl(
              securityActionsUrlsProvider.buildPasswordResetPath(
                  user.getEmail(), passwordResetToken));
      emailTemplateParams.getExtension().put(ATTR_PASSWORD_RESET_LINK, passwordResetAbsoluteLink);
      EmailMessage emailMessage =
          securityMailsMessageBuilderFactory
              .getPasswordResetEmailBuilder()
              .buildEmail(senderEmail, user.getEmail(), emailTemplateParams);
      emailSender.sendEmail(emailMessage);
    } catch (Throwable t) {
      throw new GenericRuntimeException(SecurityMessageCodes.FAILED_TO_SEND_PASSWORD_REST_EMAIL, t);
    }
  }

  public boolean isSendRegistrationConfirmationRequest() {
    return sendRegistrationConfirmationRequest;
  }

  public void setSendRegistrationConfirmationRequest(boolean sendRegistrationConfirmationRequest) {
    this.sendRegistrationConfirmationRequest = sendRegistrationConfirmationRequest;
  }

  public boolean isSendPasswordResetRequest() {
    return sendPasswordResetRequest;
  }

  public void setSendPasswordResetRequest(boolean sendPasswordResetRequest) {
    this.sendPasswordResetRequest = sendPasswordResetRequest;
  }
}
