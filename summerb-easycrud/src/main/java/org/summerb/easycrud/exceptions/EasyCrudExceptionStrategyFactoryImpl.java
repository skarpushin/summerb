package org.summerb.easycrud.exceptions;

import com.google.common.base.Preconditions;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.row.HasId;

public class EasyCrudExceptionStrategyFactoryImpl implements EasyCrudExceptionStrategyFactory {
  @Override
  public <TId extends Comparable<TId>, TRow extends HasId<TId>>
      EasyCrudExceptionStrategy<TId, TRow> create(String rowMessageCode) {
    Preconditions.checkArgument(StringUtils.hasText(rowMessageCode));

    return new EasyCrudExceptionStrategyDefaultImpl<>(rowMessageCode);
  }
}
