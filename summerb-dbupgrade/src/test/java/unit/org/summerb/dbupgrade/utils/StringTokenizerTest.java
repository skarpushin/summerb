/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package unit.org.summerb.dbupgrade.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.summerb.dbupgrade.utils.StringTokenizer;
import org.summerb.dbupgrade.utils.StringTokenizer.SubString;

public class StringTokenizerTest {
  private static final SubString COMMA = new SubString(",");
  private static final SubString ESCAPED_COMMA = new SubString("\\,");
  private static final SubString SINGLE_LINE_COMMENT = new SubString("--");
  private static final SubString MULTI_LINE_COMMENT_OPEN = new SubString("/*");
  private static final SubString MULTI_LINE_COMMENT_CLOSE = new SubString("*/");
  private static final SubString LARGE_DELIM = new SubString("&&&&&");

  @Test
  public void testNext() {
    StringTokenizer f =
        new StringTokenizer(
            "aaa,bbb--ccc/*ddd*/",
            COMMA,
            SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT_OPEN,
            MULTI_LINE_COMMENT_CLOSE);
    assertEquals("aaa", toString(f.next()));
    assertEquals(",", toString(f.next()));
    assertEquals("bbb", toString(f.next()));
    assertEquals("--", toString(f.next()));
    assertEquals("ccc", toString(f.next()));
    assertEquals("/*", toString(f.next()));
    assertEquals("ddd", toString(f.next()));
    assertEquals("*/", toString(f.next()));
    assertNull(toString(f.next()));
  }

  private String toString(SubString next) {
    if (next == null) {
      return null;
    }
    return new StringBuilder(next).toString();
  }

  @Test
  public void testNextNoDelims() {
    StringTokenizer f =
        new StringTokenizer(
            "aaaasdasdasdasdasd",
            COMMA,
            SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT_OPEN,
            MULTI_LINE_COMMENT_CLOSE);
    assertEquals("aaaasdasdasdasdasd", toString(f.next()));
    assertNull(toString(f.next()));
  }

  @Test
  public void testNextExpectDalrgeDelimDiscarded() {
    StringTokenizer f = new StringTokenizer("x,xx&&&&&xxxx", COMMA, LARGE_DELIM);
    assertEquals("x", toString(f.next()));
    assertEquals(",", toString(f.next()));
    assertEquals("xx", toString(f.next()));
    assertEquals("&&&&&", toString(f.next()));
    assertEquals("xxxx", toString(f.next()));
    assertNull(toString(f.next()));
  }

  @Test
  public void testNextExpectEscapedDelimeterRecognizedFirst() {
    StringTokenizer f = new StringTokenizer(",aaa\\,bbb,", COMMA, ESCAPED_COMMA);
    assertEquals(",", toString(f.next()));
    assertEquals("aaa", toString(f.next()));
    assertEquals("\\,", toString(f.next()));
    assertEquals("bbb", toString(f.next()));
    assertEquals(",", toString(f.next()));
    assertNull(toString(f.next()));
  }

  @Test
  public void testNextSomeDelimsRemovedRightAway() {
    StringTokenizer f =
        new StringTokenizer(
            "aaaa,sdasdasdasdasd",
            COMMA,
            SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT_OPEN,
            MULTI_LINE_COMMENT_CLOSE);
    assertEquals("aaaa", toString(f.next()));
    assertEquals(",", toString(f.next()));
    assertEquals("sdasdasdasdasd", toString(f.next()));
    assertNull(toString(f.next()));
  }
}
