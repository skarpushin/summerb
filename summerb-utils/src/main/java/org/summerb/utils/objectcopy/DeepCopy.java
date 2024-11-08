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
package org.summerb.utils.objectcopy;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.common.base.Throwables;

/**
 * Got it from: http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
 *
 * <p>Utility for making deep copies (vs. clone()'s shallow copies) of objects. Objects are first
 * serialized and then deserialized. Error checking is fairly minimal in this implementation. If an
 * object is encountered that cannot be serialized (or that references an object that cannot be
 * serialized) an error is printed to System.err and null is returned. Depending on your specific
 * application, it might make more sense to have copy(...) re-throw the exception.
 */
public class DeepCopy {

  /** Returns a copy of the object, or null if the object cannot be serialized. */
  @SuppressWarnings("unchecked")
  public static <T> T copy(T orig) {
    try {
      // Write the object out to a byte array
      FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(fbos);
      out.writeObject(orig);
      out.flush();
      out.close();

      // Retrieve an input stream from the byte array and read
      // a copy of the object back in.
      ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
      return (T) in.readObject();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to make a deep copy of the original object", e);
    }
  }

  /** Returns a copy of the object, or throws exception if the object cannot be serialized. */
  @SuppressWarnings("unchecked")
  public static <T> T copyOrPopagateExcIfAny(T orig) throws NotSerializableException {
    try {
      // Write the object out to a byte array
      FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(fbos);
      out.writeObject(orig);
      out.flush();
      out.close();

      // Retrieve an input stream from the byte array and read
      // a copy of the object back in.
      ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
      return (T) in.readObject();
    } catch (Throwable e) {
      Throwables.propagateIfPossible(e, NotSerializableException.class);
      throw new RuntimeException("Failed to make a deep copy of the original object", e);
    }
  }
}
