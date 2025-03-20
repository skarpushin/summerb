package org.summerb.email;

import java.util.Locale;

public interface EmailTemplateFactory {

  EmailTemplate build(String templateName, Locale locale);
}
