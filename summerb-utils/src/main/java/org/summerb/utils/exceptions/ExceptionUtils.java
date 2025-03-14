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
package org.summerb.utils.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sergey.karpushin
 */
public class ExceptionUtils {

  public static String getAllMessagesRaw(Throwable t) {
    if (t == null) {
      return "";
    }

    StringBuilder ret = new StringBuilder();

    Throwable cur = t;
    while (cur != null) {
      if (cur == cur.getCause()) {
        break;
      }

      if (!ret.isEmpty()) {
        ret.append(" -> ");
      }

      ret.append(cur.getClass().getSimpleName());
      ret.append(" (");
      ret.append(cur.getLocalizedMessage());
      ret.append(")");
      cur = cur.getCause();
    }

    return ret.toString();
  }

  @SuppressWarnings("unchecked")
  public static <T> T findExceptionOfType(Throwable t, Class<T> exceptionClass) {
    Throwable cur = t;
    while (cur != null) {
      if (cur == cur.getCause()) break;

      if (exceptionClass.isAssignableFrom(cur.getClass())) return (T) cur;

      cur = cur.getCause();
    }

    return null;
  }

  /**
   * Find exception cause of specified type. Will fall back to first exception if target wasn't
   * found
   *
   * @param t throwable in question
   * @param exceptionClass exception type to search for
   * @return found exception OR first exception
   */
  public static <T extends Exception> Throwable getExceptionOfClassOrFallbackToOriginal(
      Throwable t, Class<T> exceptionClass) {
    T ret = findExceptionOfType(t, exceptionClass);
    return ret == null ? t : ret;
  }

  /**
   * We want to ignore these classes when calculating exception code because these classes behave
   * different from time to time which negative effect to consistency of exception code
   */
  private static final Set<String> ignoredStackTraceClasses =
      new HashSet<>(List.of("org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor"));

  public static String calculateExceptionCode(Throwable throwable) {
    int lineCount = 0;
    int strLength = 0;
    int hash = 1;

    Throwable cur = throwable;
    while (cur != null) {
      for (int i = 0; i < throwable.getStackTrace().length; i++) {
        StackTraceElement ste = throwable.getStackTrace()[i];
        if (ste == null) {
          continue;
        }
        lineCount++;
        if (ignoredStackTraceClasses.contains(ste.getClassName())) {
          continue;
        }

        String lineNumber = String.valueOf(ste.getLineNumber());
        strLength += ste.getClassName().length();
        hash = hash * 31 + ste.getClassName().hashCode();
        strLength += ste.getMethodName().length();
        hash = hash * 31 + ste.getMethodName().hashCode();
        if (ste.getFileName() != null) {
          strLength += ste.getFileName().length();
          hash = hash * 31 + ste.getFileName().hashCode();
        }
        strLength += lineNumber.length();
        hash = hash * 31 + lineNumber.hashCode();
      }
      cur = cur.getCause();
    }

    return lineCount + "_" + strLength + "_" + (hash < 0 ? ("_" + Math.abs(hash)) : ("" + hash));
  }

  public static String getThrowableStackTraceAsString(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString(); // stack trace as a string
  }

  public static String getRootCauseMessage(Throwable t) {
    Throwable cur = t;

    String lastRootCause = null;
    while (cur != null) {
      if (cur == cur.getCause()) {
        break;
      }

      lastRootCause = cur.getMessage();

      cur = cur.getCause();
    }

    return lastRootCause;
  }
}
