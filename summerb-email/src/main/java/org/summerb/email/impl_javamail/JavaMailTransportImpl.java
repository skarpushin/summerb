package org.summerb.email.impl_javamail;

import com.google.common.base.Preconditions;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.summerb.email.JavaMailTransport;

/**
 * Simple impl which will just delegate to {@link JavaMailSender}. Later on we will replace this
 * impl with other one based on Amazon SES
 *
 * @author Sergey Karpushin
 */
public class JavaMailTransportImpl implements JavaMailTransport {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected final JavaMailSender javaMailSender;
  protected final Authenticator authenticator;

  public JavaMailTransportImpl(JavaMailSender mailSender, Authenticator authenticator) {
    super();
    this.javaMailSender = mailSender;
    this.authenticator = authenticator;
  }

  @Override
  public void sendEmail(MimeMessage message) {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Sending email to {}", Arrays.toString(message.getAllRecipients()));
      }
      javaMailSender.send(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }

  @Override
  public boolean checkConnection() {
    Preconditions.checkState(
        javaMailSender instanceof JavaMailSenderImpl,
        "sender is expected to be of type JavaMailSenderImpl");
    try {
      ((JavaMailSenderImpl) javaMailSender).testConnection();
      return true;
    } catch (MessagingException e) {
      throw new RuntimeException("emailcheck connection failed", e);
    }
  }

  @Override
  public MimeMessage newMessage() {
    return javaMailSender.createMimeMessage();
  }
}
