package org.summerb.validation;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}
