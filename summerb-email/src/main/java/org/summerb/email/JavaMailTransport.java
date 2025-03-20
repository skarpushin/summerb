package org.summerb.email;

import jakarta.mail.internet.MimeMessage;

public interface JavaMailTransport {

  /**
   * Instantiates new message.
   *
   * <p>NOTE: We have to do it this way because instantiation of {@link MimeMessage} requires
   * instance of {@link jakarta.mail.Session}
   *
   * @return fresh instance of the message
   */
  MimeMessage newMessage();

  /**
   * Send email message
   *
   * @param message Message to send
   */
  void sendEmail(MimeMessage message);

  /**
   * Check Email service availability
   *
   * @return
   */
  boolean checkConnection();
}
