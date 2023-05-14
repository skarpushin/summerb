package org.summerb.utils.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public interface NowResolver {
//
//  Instant now();
//
//  ZoneId zoneId();

  Clock clock();
}
