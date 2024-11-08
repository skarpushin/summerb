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
package org.summerb.utils.objectcopy;

import java.io.InputStream;

/** ByteArrayInputStream implementation that does not synchronize methods. */
public class FastByteArrayInputStream extends InputStream {
  /** Our byte buffer */
  protected byte[] buf;

  /** Number of bytes that we can read from the buffer */
  protected int count;

  /** Number of bytes that have been read from the buffer */
  protected int pos = 0;

  public FastByteArrayInputStream(byte[] buf, int count) {
    this.buf = buf;
    this.count = count;
  }

  @Override
  public final int available() {
    return count - pos;
  }

  @Override
  public final int read() {
    return (pos < count) ? (buf[pos++] & 0xff) : -1;
  }

  @Override
  public final int read(byte[] b, int off, int len) {
    if (pos >= count) return -1;
    int newLen = len;
    if ((pos + newLen) > count) newLen = (count - pos);

    System.arraycopy(buf, pos, b, off, newLen);
    pos += newLen;
    return newLen;
  }

  @Override
  public final long skip(long n) {
    long newN = n;
    if ((pos + newN) > count) newN = count - pos;
    if (n < 0) return 0;
    pos += newN;
    return newN;
  }
}
