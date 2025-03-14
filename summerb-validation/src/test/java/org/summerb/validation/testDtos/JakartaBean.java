package org.summerb.validation.testDtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public class JakartaBean extends JakartaBeanBase {

  /** Irrelevant field - static */
  @SuppressWarnings("unused")
  private static int staticIntValue;

  /** Irrelevant field - static final */
  protected static final int staticFinalIntValue = 1;

  /** Irrelevant field - final */
  final int finalIntValue = 2;

  /** Annotation on setter */
  private String string3;

  /** Irrelevant annotation on getter */
  private String string4;

  /** Field with no annotations and getters/setters */
  @SuppressWarnings("unused")
  private String string5;

  /** Field with irrelevant annotations */
  private String string6;

  public String getString3() {
    return string3;
  }

  @NotNull
  public void setString3(String string3) {
    this.string3 = string3;
  }

  @Autowired
  public String getString4() {
    return string4;
  }

  public void setString4(String string4) {
    this.string4 = string4;
  }

  public String getString6() {
    return string6;
  }

  public void setString6(String string6) {
    this.string6 = string6;
  }

  public void setJustSetter2(String value) {}

  public String getJustGetter2() {
    return "";
  }

  @Override
  public void setJustSetterOverride(String value) {}

  @Override
  public String getJustGetterOverride() {
    return "";
  }

  @Override
  @NotEmpty
  public String getString2() {
    return string2;
  }
}
