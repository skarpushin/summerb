package org.summerb.email.impl_javamail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class AuthenticatorSimpleImpl extends Authenticator {
  private String mailUserName;
  private String mailPassword;

  public AuthenticatorSimpleImpl(String mailUserName, String mailPassword) {
    super();
    this.mailUserName = mailUserName;
    this.mailPassword = mailPassword;
  }

  @Override
  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(mailUserName, mailPassword);
  }
}
