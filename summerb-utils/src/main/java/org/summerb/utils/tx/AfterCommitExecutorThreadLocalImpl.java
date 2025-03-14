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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This is the preferred impl of {@link Executor} - it uses thread local to store all runnables
 * which supposed to be executed only when transaction is committed
 *
 * @author sergeyk
 */
public class AfterCommitExecutorThreadLocalImpl implements TransactionSynchronization, Executor {
  private static final Logger log =
      LoggerFactory.getLogger(AfterCommitExecutorThreadLocalImpl.class);

  protected static final ThreadLocal<Queue<Runnable>> RUNNABLES = new ThreadLocal<>();

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
    Queue<Runnable> threadRunnables = RUNNABLES.get();
    if (threadRunnables == null) {
      threadRunnables = new LinkedList<>();
      RUNNABLES.set(threadRunnables);
      TransactionSynchronizationManager.registerSynchronization(this);
    }
    if (log.isDebugEnabled()) {
      log.debug("Submitting new runnable to run after commit: {}", runnable);
    }
    threadRunnables.offer(runnable);
  }

  @Override
  public void afterCommit() {
    Queue<Runnable> threadRunnables = RUNNABLES.get();
    if (threadRunnables.isEmpty()) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "Transaction successfully committed, executing runnables #{}", threadRunnables.size());
    }

    // TBD: Explain why we should do it async as it was impl before. WHy we can't
    // just run it now
    new RunRunnables(threadRunnables).run();
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
    RUNNABLES.remove();
    if (log.isDebugEnabled()) {
      String result = status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK";
      log.debug("Transaction completed with status {}", result);
    }
  }
}
