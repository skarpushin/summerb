package org.summerb.easycrud.sql_builder.impl;

import java.util.function.Supplier;

public class ParamIdxIncrementer implements Supplier<Integer> {
  protected int idx = 0;

  @Override
  public Integer get() {
    int ret = idx;
    idx++;
    return ret;
  }
}
