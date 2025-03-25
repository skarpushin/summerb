package org.summerb.webappboilerplate.thread_scope;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.summerb.utils.exceptions.ExceptionUtils;

public class ThreadScopeCleanUpFilter extends GenericFilterBean {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }

    try {
      ThreadScopeImpl.promiseToCleanUp();
      chain.doFilter(request, response);
    } catch (RuntimeException re) {
      log.warn(
          "Exception reached ThreadScopeCleanUpFilter" + ExceptionUtils.getAllMessagesRaw(re), re);
      throw re;
    } finally {
      ThreadScopeImpl.cleanup();
    }
  }
}
