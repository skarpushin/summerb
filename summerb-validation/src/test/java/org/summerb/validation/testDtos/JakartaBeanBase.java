package org.summerb.validation.testDtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class JakartaBeanBase {

  /** Annotation on field itself */
  @Size(min = 5, max = 10)
  private String string1;

  /** Annotation on getter */
  protected String string2;

  public String getString1() {
    return string1;
  }

  public void setString1(String string1) {
    this.string1 = string1;
  }

  @Email // ignored because method overridden
  public String getString2() {
    return string2;
  }

  public void setString2(String string2) {
    this.string2 = string2;
  }

  public void setJustSetter(String value) {}

  public String getJustGetter() {
    return "";
  }

  public void setJustSetterOverride(String value) {}

  public String getJustGetterOverride() {
    return "";
  }
}
