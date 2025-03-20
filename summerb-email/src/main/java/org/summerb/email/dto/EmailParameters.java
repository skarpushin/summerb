package org.summerb.email.dto;

import jakarta.mail.internet.InternetAddress;
import java.util.Map;
import java.util.Set;

/**
 * DTO used to provide parameters needed for building an email
 *
 * @author Sergey Karpushin
 */
public class EmailParameters {
  private Set<InternetAddress> to;
  private Set<InternetAddress> cc;
  private Set<InternetAddress> bcc;
  private Object templateParametersObject;
  private Map<String, byte[]> attachments;

  public Map<String, byte[]> getAttachments() {
    return attachments;
  }

  public void setAttachments(Map<String, byte[]> attachments) {
    this.attachments = attachments;
  }

  public Set<InternetAddress> getTo() {
    return to;
  }

  public void setTo(Set<InternetAddress> to) {
    this.to = to;
  }

  public Set<InternetAddress> getCc() {
    return cc;
  }

  public void setCc(Set<InternetAddress> cc) {
    this.cc = cc;
  }

  public Set<InternetAddress> getBcc() {
    return bcc;
  }

  public void setBcc(Set<InternetAddress> bcc) {
    this.bcc = bcc;
  }

  public Object getTemplateParametersObject() {
    return templateParametersObject;
  }

  public void setTemplateParametersObject(Object templateParametersObject) {
    this.templateParametersObject = templateParametersObject;
  }
}
