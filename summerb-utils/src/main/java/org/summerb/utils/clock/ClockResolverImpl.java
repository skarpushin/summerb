package org.summerb.utils.clock;

import com.google.common.base.Preconditions;
import java.time.Clock;

public class ClockResolverImpl implements ClockResolver {
  private Clock clock;

  public ClockResolverImpl() {
    this.clock = Clock.systemUTC();
  }

  public ClockResolverImpl(Clock clock) {
    Preconditions.checkArgument(clock != null);
    this.clock = clock;
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
