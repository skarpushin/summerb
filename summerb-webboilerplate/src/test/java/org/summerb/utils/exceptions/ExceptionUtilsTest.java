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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExceptionUtilsTest {

  @Test
  public void testCalculateExceptionCode() {
    String code = null;
    for (int i = 0; i < 15; i++) {
      try {
        doThrow(i);
      } catch (Throwable t) {
        if (code == null) {
          code = ExceptionUtils.calculateExceptionCode(t);
        } else {
          assertEquals(code, ExceptionUtils.calculateExceptionCode(t));
        }
      }
    }
  }

  protected void doThrow(int i) {
    throw new IllegalStateException("Some test exception with additional data: " + i);
  }
}
