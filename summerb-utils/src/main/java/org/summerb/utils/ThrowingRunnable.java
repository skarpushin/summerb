package org.summerb.utils;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}
