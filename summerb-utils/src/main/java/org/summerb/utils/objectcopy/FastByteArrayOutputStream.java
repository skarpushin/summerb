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
package org.summerb.utils.objectcopy;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * ByteArrayOutputStream implementation that doesn't synchronize methods and doesn't copy the data
 * on toByteArray().
 */
public class FastByteArrayOutputStream extends OutputStream {
  /** Buffer and size */
  protected byte[] buf;

  protected int size;

  /** Constructs a stream with buffer capacity size 5K */
  public FastByteArrayOutputStream() {
    this(5 * 1024);
  }

  /** Constructs a stream with the given initial size */
  public FastByteArrayOutputStream(int initSize) {
    this.size = 0;
    this.buf = new byte[initSize];
  }

  /** Ensures that we have a large enough buffer for the given size. */
  private void verifyBufferSize(int sz) {
    if (sz > buf.length) {
      byte[] old = buf;
      buf = new byte[Math.max(sz, 2 * buf.length)];
      System.arraycopy(old, 0, buf, 0, old.length);
    }
  }

  public int getSize() {
    return size;
  }

  /**
   * Returns the byte array containing the written data. Note that this array will almost always be
   * larger than the amount of data actually written.
   */
  public byte[] getByteArray() {
    return buf;
  }

  @Override
  public final void write(byte[] b) {
    verifyBufferSize(size + b.length);
    System.arraycopy(b, 0, buf, size, b.length);
    size += b.length;
  }

  @Override
  public final void write(byte[] b, int off, int len) {
    verifyBufferSize(size + len);
    System.arraycopy(b, off, buf, size, len);
    size += len;
  }

  @Override
  public final void write(int b) {
    verifyBufferSize(size + 1);
    buf[size++] = (byte) b;
  }

  public void reset() {
    size = 0;
  }

  /** Returns a ByteArrayInputStream for reading back the written data */
  public InputStream getInputStream() {
    return new FastByteArrayInputStream(buf, size);
  }
}
