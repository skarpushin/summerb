package org.summerb.utils.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.google.common.base.Preconditions;

public class NowResolverImpl implements NowResolver {
  private Clock clock;

  public NowResolverImpl() {
    this.clock = Clock.systemUTC();
  }

  public NowResolverImpl(Clock clock) {
    Preconditions.checkArgument(clock != null);
    this.clock = clock;
  }

  //  @Override
  //  public Instant now() {
  //    if (clock == null) {
  //      return Instant.now();
  //    } else {
  //      return Instant.now(clock);
  //    }
  //  }
  //
  //  @Override
  //  public ZoneId zoneId() {
  //    return clock.getZone();
  //  }

  @Override
  public Clock clock() {
    return clock;
  }
}
