package org.summerb.validation;

import java.util.Arrays;
import java.util.Objects;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;

public class I18nMessage implements HasMessageCode, HasMessageArgs {

  private String messageCode;
  private Object[] messageArgs;

  public I18nMessage() {}

  public I18nMessage(String messageCode, Object... messageArgs) {
    super();
    this.messageCode = messageCode;
    this.messageArgs = messageArgs;
  }

  @Override
  public String getMessageCode() {
    return messageCode;
  }

  @Override
  public Object[] getMessageArgs() {
    return messageArgs;
  }

  public void setMessageCode(String messageCode) {
    this.messageCode = messageCode;
  }

  public void setMessageArgs(Object[] messageArgs) {
    this.messageArgs = messageArgs;
  }

  @Override
  public String toString() {
    return "I18nMessage [messageCode="
        + messageCode
        + ", messageArgs="
        + Arrays.toString(messageArgs)
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.deepHashCode(messageArgs);
    result = prime * result + Objects.hash(messageCode);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    I18nMessage other = (I18nMessage) obj;
    return Arrays.deepEquals(messageArgs, other.messageArgs)
        && Objects.equals(messageCode, other.messageCode);
  }
}
