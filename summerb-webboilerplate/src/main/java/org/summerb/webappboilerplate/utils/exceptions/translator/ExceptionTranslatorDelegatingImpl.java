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
package org.summerb.webappboilerplate.utils.exceptions.translator;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ExceptionTranslatorDelegatingImpl implements ExceptionTranslator {
  protected List<ExceptionTranslator> translators;
  protected ExceptionUnwindingStrategy exceptionUnwindingStrategy =
      new ExceptionUnwindingStrategyImpl();
  protected String joinerString = " -> ";

  public ExceptionTranslatorDelegatingImpl(List<ExceptionTranslator> translators) {
    this.translators = new LinkedList<>(translators);
  }

  @Override
  public String buildUserMessage(Throwable t, Locale locale) {
    if (t == null) {
      return "";
    }

    StringBuilder ret = new StringBuilder();

    Throwable cur = exceptionUnwindingStrategy.getNextMeaningfulExc(t);
    while (cur != null) {
      if (ret.length() > 0) {
        appendJoiner(ret);
      }

      boolean matchFound = false;
      for (ExceptionTranslator translator : translators) {
        String msg = translator.buildUserMessage(cur, locale);
        if (msg == null) {
          continue;
        }

        ret.append(msg);
        matchFound = true;
        break;
      }
      if (!matchFound) {
        ret.append(t.getMessage());
      }

      if (cur == cur.getCause()) {
        break;
      }
      cur = exceptionUnwindingStrategy.getNextMeaningfulExc(cur.getCause());
    }
    return ret.toString();
  }

  /**
   * Add joiner between messages. Do not add part of the {@link #joinerString} that is similar to
   * the current ending
   */
  protected void appendJoiner(StringBuilder ret) {
    if ("".equals(joinerString)) {
      return;
    }

    int scanFrom = Math.max(0, ret.length() - joinerString.length());

    int pos1 = scanFrom;
    int pos2 = 0;

    for (; pos1 < ret.length(); pos1++) {
      char charSubject = ret.charAt(pos1);
      char charExpected = joinerString.charAt(pos2);
      if (charSubject == charExpected) {
        break;
      }
    }

    for (int i = pos1; pos2 < joinerString.length() && i < ret.length(); pos2++, i++) {
      char charSubject = ret.charAt(i);
      char charExpected = joinerString.charAt(pos2);
      if (charSubject != charExpected) {
        break;
      }
    }

    if (pos2 == 0) {
      ret.append(joinerString);
    } else if (pos2 == joinerString.length()) {
      return;
    } else {
      ret.append(joinerString.substring(pos2));
    }
  }

  public ExceptionUnwindingStrategy getExceptionUnwindingStrategy() {
    return exceptionUnwindingStrategy;
  }

  public void setExceptionUnwindingStrategy(ExceptionUnwindingStrategy exceptionUnwindingStrategy) {
    this.exceptionUnwindingStrategy = exceptionUnwindingStrategy;
  }

  public String getJoinerString() {
    return joinerString;
  }

  public void setJoinerString(String joinerString) {
    this.joinerString = joinerString;
  }
}
