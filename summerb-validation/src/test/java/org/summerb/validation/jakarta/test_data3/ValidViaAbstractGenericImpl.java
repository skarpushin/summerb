package org.summerb.validation.jakarta.test_data3;

import javax.validation.constraints.Min;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ValidViaAbstractGenericImpl extends AbstractGenericImpl<Boolean>
    implements HasMessageCode {

  public ValidViaAbstractGenericImpl(Min annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    ctx.notNull(value, propertyName);
  }

  @Override
  public String getMessageCode() {
    return null;
  }

  @Override
  @SuppressFBWarnings(
      value = "NP_BOOLEAN_RETURN_NULL",
      justification = "this is just test dto, never used, needed only for introspection")
  protected Boolean get() {
    return null;
  }
}
