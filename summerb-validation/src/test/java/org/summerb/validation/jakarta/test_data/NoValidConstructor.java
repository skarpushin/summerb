package org.summerb.validation.jakarta.test_data;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Size;

public class NoValidConstructor extends AnnotationProcessorNullableAbstract<Size> {

  @SuppressFBWarnings(
      value = "NP_NONNULL_PARAM_VIOLATION",
      justification = "this is just test dto, never used, needed only for introspection")
  public NoValidConstructor(String asd, int a, int b) {
    super(null, null);
  }

  @SuppressFBWarnings(
      value = "NP_NONNULL_PARAM_VIOLATION",
      justification = "this is just test dto, never used, needed only for introspection")
  public NoValidConstructor(String asd, int a) {
    super(null, null);
  }

  @SuppressFBWarnings(
      value = "NP_NONNULL_PARAM_VIOLATION",
      justification = "this is just test dto, never used, needed only for introspection")
  public NoValidConstructor(Size annotation, boolean bValue) {
    super(null, null);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {}
}
