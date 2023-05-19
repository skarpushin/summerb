package org.summerb.validation.testDtos;

import javax.validation.constraints.Size;

/** It is invalid because field does not have matching getter/setter */
public class JakartaBeanInvalid1 {

  @Size(min = 5, max = 10)
  private String string1;
}
