package org.summerb.validation.testDtos;

import javax.validation.constraints.Size;

/** It is invalid because getter throws exception */
public class JakartaBeanInvalid3 {

  @Size(min = 3, max = 7)
  private String throwsException;

  public String getThrowsException() {
    throw new IllegalStateException("test");
  }

  public void setThrowsException(String throwsException) {
    this.throwsException = throwsException;
  }
}
