package org.summerb.easycrud.api.query.restrictions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class In extends NegateableRestriction<In> {

  protected Set<?> values;

  public In(Collection<?> values) {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(values), "Non empty collection expected");
    // NOTE: We're copying collection state as a precaution - so we don't depend on outer state
    this.values = new HashSet<>(values);
  }

  public Set<?> getValues() {
    return values;
  }

  public void setValues(Set<?> values) {
    this.values = values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    In in = (In) o;
    return Objects.equals(values, in.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), values);
  }
}
