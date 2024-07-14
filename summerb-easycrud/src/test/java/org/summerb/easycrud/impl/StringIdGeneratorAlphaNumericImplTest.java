/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.easycrud.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.summerb.easycrud.api.StringIdGenerator;

public class StringIdGeneratorAlphaNumericImplTest {
  @Test
  public void testGenerateNewId() {
    StringIdGenerator f = new StringIdGeneratorAlphaNumericImpl();
    String result = f.generateNewId(null);
    assertNotNull(result);
    assertEquals(8, result.length());
  }

  @Test
  public void testIsValidId() {
    StringIdGenerator f = new StringIdGeneratorAlphaNumericImpl();

    for (int i = 0; i < 1000; i++) {
      String result = f.generateNewId(null);
      // System.out.println("ID generated: " + result);

      assertNotNull(result);
      assertEquals(8, result.length());
      assertTrue(f.isValidId(result));
    }
  }
}
