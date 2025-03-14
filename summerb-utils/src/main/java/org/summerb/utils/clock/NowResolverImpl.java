package org.summerb.utils.clock;

import com.google.common.base.Preconditions;
import java.time.Clock;

public class NowResolverImpl implements NowResolver {
  private Clock clock;

  public NowResolverImpl() {
    this.clock = Clock.systemUTC();
  }

  public NowResolverImpl(Clock clock) {
    Preconditions.checkArgument(clock != null);
    this.clock = clock;
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
