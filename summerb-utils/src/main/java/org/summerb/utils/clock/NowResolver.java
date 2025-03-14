package org.summerb.utils.clock;

import java.time.Clock;

public interface NowResolver {

  Clock clock();
}
