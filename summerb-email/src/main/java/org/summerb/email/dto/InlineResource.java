package org.summerb.email.dto;

import org.springframework.core.io.ByteArrayResource;

public class InlineResource {
  private ByteArrayResource bytes;
  private String contentType;

  public InlineResource() {}

  public InlineResource(ByteArrayResource bytes, String mediaType) {
    super();
    this.bytes = bytes;
    this.contentType = mediaType;
  }

  public ByteArrayResource getBytes() {
    return bytes;
  }

  public void setBytes(ByteArrayResource bytes) {
    this.bytes = bytes;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String mediaType) {
    this.contentType = mediaType;
  }
}
