package org.summerb.validation.jakarta.test_data;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.AnnotationProcessor;

import javax.validation.constraints.AssertTrue;

public class ValidProcessorThatImplements
    implements AnnotationProcessor<AssertTrue>, HasMessageCode {

  private String propertyName;

  public ValidProcessorThatImplements(AssertTrue annotation, String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    ctx.isTrue((Boolean) value, propertyName);
  }

  @Override
  public String getMessageCode() {
    return null;
  }
}
