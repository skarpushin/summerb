package org.summerb.validation.testDtos;

import javax.validation.constraints.Size;

/** It is invalid because field does not have matching getter/setter */
public class JakartaBeanInvalid2 extends JakartaBeanInvalid2Base {

  @Size(min = 3, max = 7)
  private String stringDuplicateInBaseClass;
}
