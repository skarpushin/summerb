package org.summerb.easycrud.api.query.restrictions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class In extends NegateableRestriction<In> {

  private Set<?> values;

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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    In other = (In) obj;
    if (values == null) {
      if (other.values != null) {
        return false;
      }
    } else if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }
}
