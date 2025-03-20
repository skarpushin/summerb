package org.summerb.email.impl;

import com.google.common.base.Preconditions;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.summerb.email.EmailTransport;
import org.summerb.email.dto.Email;
import org.summerb.validation.ValidationContext;

public class EmailTransportRecipientOverrideWrapper implements EmailTransport {
  protected final EmailTransport actual;
  protected final InternetAddress[] to;

  public EmailTransportRecipientOverrideWrapper(
      EmailTransport actual, String replaceAllRecipientsWith) {
    Preconditions.checkArgument(actual != null, "actual required");
    Preconditions.checkArgument(
        ValidationContext.isValidEmail(replaceAllRecipientsWith),
        "replaceAllRecipientsWith must be a valid email");

    this.actual = actual;

    try {
      this.to = new InternetAddress[] {new InternetAddress(replaceAllRecipientsWith)};
    } catch (AddressException e) {
      throw new RuntimeException(
          "Failed to convert email " + replaceAllRecipientsWith + " to InternetAddress", e);
    }
  }

  @Override
  public boolean checkConnection() {
    return actual.checkConnection();
  }

  @Override
  public void sendEmail(Email email) {
    Email emailModified = new Email();

    // same as original
    emailModified.setAttachments(email.getAttachments());
    emailModified.setInline(email.getInline());
    emailModified.setSubject(email.getSubject());
    emailModified.setText(email.getText());

    // override recipients
    emailModified.setBcc(null);
    emailModified.setCc(null);
    emailModified.setTo(to);

    actual.sendEmail(emailModified);
  }
}
