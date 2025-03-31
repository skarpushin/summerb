package org.summerb.utils;

@FunctionalInterface
public interface UncheckedCallable<V> {
  V call();
}
