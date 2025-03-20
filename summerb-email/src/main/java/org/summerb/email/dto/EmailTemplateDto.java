package org.summerb.email.dto;

import java.util.Locale;
import java.util.Map;

/**
 * This DTO is used for IO purposes to load email template
 *
 * @author Sergey Karpushin
 */
public class EmailTemplateDto {
  private String subjectTemplate;
  private String bodyTemplate;
  private Map<String, String> images;
  private Locale locale;

  public String getSubjectTemplate() {
    return subjectTemplate;
  }

  public void setSubjectTemplate(String subject) {
    this.subjectTemplate = subject;
  }

  public String getBodyTemplate() {
    return bodyTemplate;
  }

  public void setBodyTemplate(String body) {
    this.bodyTemplate = body;
  }

  public Map<String, String> getImages() {
    return images;
  }

  public void setImages(Map<String, String> images) {
    this.images = images;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
