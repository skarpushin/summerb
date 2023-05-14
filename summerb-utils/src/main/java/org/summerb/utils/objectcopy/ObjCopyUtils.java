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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Preconditions;

public class ObjCopyUtils {
  /**
   * Get property values from source object (using getters) and assign to destination (using
   * setters)
   *
   * @param src source object
   * @param dst target object. source class expected to be assignable from destination
   */
  public static <TSrc, TDst extends TSrc> void assignFields(TSrc src, TDst dst) {
    Preconditions.checkArgument(src != null, "Src must not be null");
    Preconditions.checkArgument(dst != null, "Dst must not be null");
    Preconditions.checkArgument(
        src.getClass().isAssignableFrom(dst.getClass()),
        "source class expected to be assignable from destination");

    try {
      PropertyDescriptor[] srcProps = BeanUtils.getPropertyDescriptors(src.getClass());
      for (PropertyDescriptor pd : srcProps) {
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
          continue;
        }

        Object value = pd.getReadMethod().invoke(src);
        writeMethod.invoke(dst, value);
      }
    } catch (Throwable t) {
      throw new RuntimeException("Failed to copy properties from " + src + " to " + dst, t);
    }
  }
}
