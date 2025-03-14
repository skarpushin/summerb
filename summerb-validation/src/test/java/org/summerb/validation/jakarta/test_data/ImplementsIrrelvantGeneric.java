package org.summerb.validation.jakarta.test_data;

import jakarta.validation.constraints.AssertFalse;
import java.util.function.Predicate;

public class ImplementsIrrelvantGeneric implements Predicate<Boolean> {

  public ImplementsIrrelvantGeneric(AssertFalse annotation, String propertyName) {}

  @Override
  public boolean test(Boolean t) {
    return false;
  }
}
