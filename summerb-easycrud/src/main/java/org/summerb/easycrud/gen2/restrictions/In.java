package org.summerb.easycrud.gen2.restrictions;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

public class In extends NegateableRestrictionEx<In> {

  public In(Collection<Object> values) {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(values), "Non empty collection expected");
    // TODO Auto-generated constructor stub
  }
}
