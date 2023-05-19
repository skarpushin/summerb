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
package org.summerb.webappboilerplate.utils.exceptions.translator;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;

public class ExceptionTranslatorDelegatingImplTest {
  @Test
  public void testAppendJoiner1() {
    ExceptionTranslatorDelegatingImpl f =
        new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
    f.setJoinerString(". ");

    StringBuilder ret = new StringBuilder();
    ret.append("Asd. ");
    f.appendJoiner(ret);

    assertEquals("Asd. ", ret.toString());
  }

  @Test
  public void testAppendJoiner2() {
    ExceptionTranslatorDelegatingImpl f =
        new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
    f.setJoinerString(". ");

    StringBuilder ret = new StringBuilder();
    ret.append("Asd");
    f.appendJoiner(ret);

    assertEquals("Asd. ", ret.toString());
  }

  @Test
  public void testAppendJoiner3() {
    ExceptionTranslatorDelegatingImpl f =
        new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
    f.setJoinerString(". ");

    StringBuilder ret = new StringBuilder();
    ret.append("Asd.");
    f.appendJoiner(ret);

    assertEquals("Asd. ", ret.toString());
  }

  @Test
  public void testAppendJoiner4() {
    ExceptionTranslatorDelegatingImpl f =
        new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
    f.setJoinerString(". ");

    StringBuilder ret = new StringBuilder();
    ret.append("asd. asd");
    f.appendJoiner(ret);

    assertEquals("asd. asd. ", ret.toString());
  }
}
