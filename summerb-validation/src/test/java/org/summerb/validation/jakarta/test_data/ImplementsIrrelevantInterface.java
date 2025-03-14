package org.summerb.validation.jakarta.test_data;

import jakarta.validation.constraints.AssertFalse;
import org.summerb.i18n.HasMessageCode;

public class ImplementsIrrelevantInterface implements HasMessageCode {

  public ImplementsIrrelevantInterface(AssertFalse annotation, String propertyName) {}

  @Override
  public String getMessageCode() {
    return null;
  }
}
