package org.summerb.email.impl;

import com.google.common.base.Preconditions;
import java.util.Locale;
import org.summerb.email.EmailTemplate;
import org.summerb.email.EmailTemplateFactory;
import org.summerb.email.EmailTransport;
import org.summerb.email.TemplateAwareSender;
import org.summerb.email.dto.Email;
import org.summerb.email.dto.EmailParameters;

public class TemplateAwareSenderImpl implements TemplateAwareSender {
  protected final EmailTemplateFactory emailTemplateFactory;
  protected final EmailTransport emailTransport;

  public TemplateAwareSenderImpl(
      EmailTemplateFactory emailTemplateFactory, EmailTransport emailTransport) {
    Preconditions.checkNotNull(emailTemplateFactory, "emailTemplateFactory required");
    Preconditions.checkNotNull(emailTemplateFactory, "emailTransport required");

    this.emailTemplateFactory = emailTemplateFactory;
    this.emailTransport = emailTransport;
  }

  @Override
  public void sendEmail(String templateName, Locale locale, EmailParameters emailParameters) {
    try {
      EmailTemplate template = emailTemplateFactory.build(templateName, locale);
      Email email = template.apply(emailParameters);
      emailTransport.sendEmail(email);
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Error while sending email " + templateName + " locale " + locale, ex);
    }
  }
}
