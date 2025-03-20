package org.summerb.email;

import org.summerb.email.dto.Email;

public interface EmailTransport {

  boolean checkConnection();

  void sendEmail(Email email);
}
