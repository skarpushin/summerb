package org.summerb.validation.jakarta.test_data;

import java.util.function.Predicate;

import javax.validation.constraints.AssertFalse;

public class ImplementsIrrelvantGeneric implements Predicate<Boolean> {

  public ImplementsIrrelvantGeneric(AssertFalse annotation, String propertyName) {}

  @Override
  public boolean test(Boolean t) {
    return false;
  }
}
