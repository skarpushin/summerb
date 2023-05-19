package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustNotEndWith extends ValidationError {
  private static final long serialVersionUID = -5415374244230844232L;

  public static final String MESSAGE_CODE = "validation.mustNot.endWith";

  /** @deprecated used only for serialization */
  public MustNotEndWith() {}

  public MustNotEndWith(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
