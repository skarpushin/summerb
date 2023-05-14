package org.summerb.utils.clock;

import java.time.Clock;

public interface NowResolver {
  //
  //  Instant now();
  //
  //  ZoneId zoneId();

  Clock clock();
}
