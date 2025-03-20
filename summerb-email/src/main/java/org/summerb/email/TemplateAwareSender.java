package org.summerb.email;

import java.util.Locale;
import org.summerb.email.dto.EmailParameters;

public interface TemplateAwareSender {

  void sendEmail(String templateName, Locale locale, EmailParameters emailParameters);
}
