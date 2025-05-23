/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.utils.tx;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This impl is intended to be scoped for each request. It will NOT work properly if instantiated as
 * a singleton.
 *
 * @author sergeyk
 */
public class AfterCommitExecutorImpl implements TransactionSynchronization, Executor {
  protected static final Logger log = LoggerFactory.getLogger(AfterCommitExecutorImpl.class);

  protected ExecutorService executorService;
  protected Queue<Runnable> threadRunnables;

  public AfterCommitExecutorImpl(ExecutorService executorService) {
    Preconditions.checkArgument(executorService != null, "executorService required");
    this.executorService = executorService;
  }

  @Override
  public void execute(Runnable runnable) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      if (log.isDebugEnabled()) {
        log.debug(
            "Transaction synchronization is NOT ACTIVE. Executing runnable right now {}", runnable);
      }
      safeRun(runnable);
      return;
    }
    if (threadRunnables == null) {
      threadRunnables = new LinkedList<>();
      TransactionSynchronizationManager.registerSynchronization(this);
    }
    if (log.isDebugEnabled()) {
      log.debug("Submitting new runnable to run after commit: {}", runnable);
    }
    threadRunnables.offer(runnable);
  }

  @Override
  public void afterCommit() {
    if (threadRunnables.isEmpty()) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "Transaction successfully committed, executing runnables #{}", threadRunnables.size());
    }

    // NOTE: We do it in separate thread to ensure they will be executed
    // outside of current transaction.Guaranteed
    executorService.submit(new RunRunnables(threadRunnables));
  }

  protected static class RunRunnables implements Runnable {
    protected Queue<Runnable> threadRunnables;

    public RunRunnables(Queue<Runnable> threadRunnables) {
      this.threadRunnables = threadRunnables;
    }

    @Override
    public void run() {
      Runnable runnable;
      while ((runnable = threadRunnables.poll()) != null) {
        if (log.isDebugEnabled()) {
          log.debug("Executing runnable after TX commit{}", runnable);
        }
        safeRun(runnable);
      }
    }
  }

  protected static void safeRun(Runnable runnable) {
    try {
      runnable.run();
    } catch (Throwable e) {
      log.error("Failed to execute runnable on transaction commit {}", runnable, e);
    }
  }

  @Override
  public void afterCompletion(int status) {
    if (log.isDebugEnabled()) {
      String result = status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK";
      log.debug("Transaction completed with status {}", result);
    }
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }
}
