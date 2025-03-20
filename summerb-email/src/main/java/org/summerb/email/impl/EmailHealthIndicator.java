package org.summerb.email.impl;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.summerb.email.EmailTransport;
import org.summerb.utils.exceptions.ExceptionUtils;

public class EmailHealthIndicator implements HealthIndicator {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected final EmailTransport emailTransport;

  public EmailHealthIndicator(EmailTransport emailTransport) {
    Preconditions.checkNotNull(emailTransport, "emailTransport required");
    this.emailTransport = emailTransport;
  }

  @Override
  public Health health() {
    try {
      if (emailTransport.checkConnection()) {
        return Health.up().build();
      } else {
        return Health.down()
            .withDetail("exception", "checkConnection method returned false")
            .build();
      }
    } catch (Throwable e) {
      log.warn("Email health check failed", e);
      return Health.down().withDetail("exception", ExceptionUtils.getAllMessagesRaw(e)).build();
    }
  }
}
