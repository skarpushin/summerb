package org.summerb.email.dto;

import jakarta.mail.internet.InternetAddress;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.ByteArrayResource;

public class Email implements Serializable {
  private static final long serialVersionUID = -7860044117715437155L;

  private InternetAddress[] to;
  private InternetAddress[] cc;
  private InternetAddress[] bcc;
  private String subject;
  private String text;
  private Map<String, ByteArrayResource> attachments = new HashMap<>();
  private Map<String, InlineResource> inline = new HashMap<>();

  public void addAttachment(String key, ByteArrayResource byteArrayResource) {
    attachments.put(key, byteArrayResource);
  }

  public void addInline(String key, InlineResource inlineResource) {
    inline.put(key, inlineResource);
  }

  public InternetAddress[] getTo() {
    return to;
  }

  public void setTo(InternetAddress[] to) {
    this.to = to;
  }

  public InternetAddress[] getCc() {
    return cc;
  }

  public void setCc(InternetAddress[] cc) {
    this.cc = cc;
  }

  public InternetAddress[] getBcc() {
    return bcc;
  }

  public void setBcc(InternetAddress[] bcc) {
    this.bcc = bcc;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getText() {
    return text;
  }

  public void setText(String body) {
    this.text = body;
  }

  public Map<String, ByteArrayResource> getAttachments() {
    return attachments;
  }

  public void setAttachments(Map<String, ByteArrayResource> attachments) {
    this.attachments = attachments;
  }

  public Map<String, InlineResource> getInline() {
    return inline;
  }

  public void setInline(Map<String, InlineResource> inline) {
    this.inline = inline;
  }
}
