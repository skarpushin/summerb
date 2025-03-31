package org.summerb.webappboilerplate.thread_scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

/**
 * Mark your beans with {@link ThreadScope} annotation to make them managed by this scope. It is
 * useful in cases when {@link org.springframework.web.context.annotation.RequestScope} is not
 * enough in a sense that sometimes there is no request (like in a background job)
 *
 * <p>In order for this annotation to work, make sure to register static bean:
 *
 * <pre>
 *   &#064;Bean
 *   static ThreadScopeBeanFactoryPostProcessor threadScopeBeanFactoryPostProcessor() {
 *     return new ThreadScopeBeanFactoryPostProcessor();
 *   }
 * </pre>
 *
 * Register servlet filter:
 *
 * <pre>
 *   &#064;Bean
 *   public FilterRegistrationBean&lt;ThreadScopeCleanUpFilter&gt; threadScopeCleanUpFilter() {
 *     FilterRegistrationBean&lt;ThreadScopeCleanUpFilter&gt; ret = new FilterRegistrationBean&lt;&gt;();
 *     ret.setFilter(new ThreadScopeCleanUpFilter());
 *     ret.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
 *     return ret;
 *   }
 * </pre>
 *
 * And wrap all your other execution entry points (like schedulers and threads) with
 *
 * <pre>
 *   try {
 *     ThreadScopeImpl.promiseToCleanUp();
 *     // your code
 *   } finally {
 *     ThreadScopeImpl.cleanup();
 *   }
 * </pre>
 *
 * @author Sergey Karpushin
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Scope(ThreadScopeImpl.NAME)
public @interface ThreadScope {

  /**
   * Alias for {@link Scope#proxyMode}.
   *
   * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
   */
  @AliasFor(annotation = Scope.class)
  ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}
