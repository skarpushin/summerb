package org.summerb.validation.testDtos;

import jakarta.validation.constraints.Size;

public class JakartaBeanInvalid2Base {

  @Size(min = 3, max = 7)
  private String stringDuplicateInBaseClass;

  public String getStringDuplicateInBaseClass() {
    return stringDuplicateInBaseClass;
  }

  public void setStringDuplicateInBaseClass(String stringDuplicateInBaseClass) {
    this.stringDuplicateInBaseClass = stringDuplicateInBaseClass;
  }
}
