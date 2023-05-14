package org.summerb.validation.jakarta.test_data;

import org.summerb.i18n.HasMessageCode;

import jakarta.validation.constraints.AssertFalse;

public class ImplementsIrrelvantInterface implements HasMessageCode {

  public ImplementsIrrelvantInterface(AssertFalse annotation, String propertyName) {}

  @Override
  public String getMessageCode() {
    return null;
  }
}
