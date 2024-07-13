package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

public class Equals extends NegateableRestriction<Equals> {

  protected Object value;

  public Equals(Object value) {
    Preconditions.checkArgument(value != null, "value required");
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Equals equals = (Equals) o;
    return Objects.equals(value, equals.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }
}
