package org.summerb.email.impl_javamail;

import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.summerb.email.EmailTransport;
import org.summerb.email.JavaMailTransport;
import org.summerb.email.dto.Email;

public class EmailTransportViaJavaMailImpl implements EmailTransport {
  protected final JavaMailTransport javaMailTransport;

  public EmailTransportViaJavaMailImpl(JavaMailTransport javaMailTransport) {
    this.javaMailTransport = javaMailTransport;
  }

  @Override
  public boolean checkConnection() {
    return javaMailTransport.checkConnection();
  }

  @Override
  public void sendEmail(Email email) {
    try {
      MimeMessageHelper helper =
          new MimeMessageHelper(javaMailTransport.newMessage(), true, "UTF-8");
      // helper.setFrom(email.getFrom());// NOTE: From must be defined via sender
      // impl if needed, not via message
      helper.setTo(email.getTo());
      if (email.getCc() != null) {
        helper.setCc(email.getCc());
      }
      if (email.getBcc() != null) {
        helper.setBcc(email.getBcc());
      }
      helper.setSubject(email.getSubject());
      helper.setText(email.getText(), true);

      email
          .getInline()
          .forEach(
              (k, v) -> {
                try {
                  helper.addInline(k, v.getBytes(), v.getContentType());
                } catch (MessagingException e) {
                  throw new RuntimeException("Failedto add inline resource " + k, e);
                }
              });
      email
          .getAttachments()
          .forEach(
              (k, v) -> {
                try {
                  helper.addAttachment(k, v);
                } catch (MessagingException e) {
                  throw new RuntimeException("Failedto add attachment " + k, e);
                }
              });

      javaMailTransport.sendEmail(helper.getMimeMessage());
    } catch (Exception e) {
      throw new RuntimeException("sendEmail failed", e);
    }
  }
}
