package org.summerb.webappboilerplate.thread_scope;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

/**
 * Implementation of the {@link ThreadScope}
 *
 * <p>NOTE: Since this is Thread scope - no synchronization needed
 */
public class ThreadScopeImpl implements Scope {
  public static final String NAME = "Thread";

  private static final ThreadLocal<ThreadContext> CTX = ThreadLocal.withInitial(ThreadContext::new);

  public static void promiseToCleanUp() {
    Preconditions.checkState(!isThreadScopeConfigured(), "promiseToCleanUp was already called");
    CTX.get().cleanupTriggerConfigured = true;
  }

  public static void cleanup() {
    Preconditions.checkState(isThreadScopeConfigured(), "ThreadScopeImpl was not initialized");
    CTX.get().cleanupTriggerConfigured = false;
    if (CTX.get().destructionCallbacks.isEmpty() && CTX.get().beans.isEmpty()) {
      return;
    }

    for (Runnable runnable : CTX.get().destructionCallbacks.values()) {
      runnable.run();
    }
    CTX.get().destructionCallbacks.clear();
    CTX.get().beans.clear();
  }

  public static boolean isThreadScopeConfigured() {
    return CTX.get().cleanupTriggerConfigured;
  }

  @Override
  public Object get(String name, ObjectFactory<?> objectFactory) {
    Preconditions.checkState(isThreadScopeConfigured(), "ThreadScopeImpl was not initialized");

    Object scopedObject = CTX.get().beans.get(name);
    if (scopedObject == null) {
      scopedObject = objectFactory.getObject();
      CTX.get().beans.put(name, scopedObject);
    }
    return scopedObject;
  }

  @Override
  @Nullable
  public Object remove(String name) {
    Preconditions.checkState(isThreadScopeConfigured(), "ThreadScopeImpl was not initialized");
    Object scopedObject = CTX.get().beans.remove(name);

    if (scopedObject == null) {
      return null;
    }

    CTX.get().destructionCallbacks.remove(name);
    return scopedObject;
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    Preconditions.checkState(isThreadScopeConfigured(), "ThreadScopeImpl was not initialized");
    CTX.get().destructionCallbacks.put(name, callback);
  }

  @Override
  @Nullable
  public Object resolveContextualObject(String key) {
    return null;
  }

  @Override
  public String getConversationId() {
    return Thread.currentThread().getName();
  }

  private static class ThreadContext {
    boolean cleanupTriggerConfigured;
    Map<String, Runnable> destructionCallbacks = new LinkedHashMap<>();
    Map<String, Object> beans = new HashMap<>();
  }
}
