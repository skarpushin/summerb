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

import com.google.common.base.Preconditions;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.springframework.beans.BeanUtils;

public class BeanPropertiesUtils {
  /**
   * Get property values from source object (using getters) and assign to destination (using
   * setters) matching them by name
   *
   * @param from source object
   * @param to target object. source class expected to be assignable from destination
   */
  public static <TSrc, TDst extends TSrc> void copy(TSrc from, TDst to) {
    Preconditions.checkArgument(from != null, "Src must not be null");
    Preconditions.checkArgument(to != null, "Dst must not be null");
    Preconditions.checkArgument(
        from.getClass().isAssignableFrom(to.getClass()),
        "source class expected to be assignable from destination");

    try {
      PropertyDescriptor[] srcProps = BeanUtils.getPropertyDescriptors(from.getClass());
      for (PropertyDescriptor pd : srcProps) {
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null || writeMethod.getParameterCount() != 1) {
          continue;
        }

        Class<?> sourceType = pd.getReadMethod().getReturnType();
        Class<?> targetType = writeMethod.getParameters()[0].getType();
        if (!targetType.isAssignableFrom(sourceType)) {
          return;
        }

        Object value = pd.getReadMethod().invoke(from);
        writeMethod.invoke(to, value);
      }
    } catch (Throwable t) {
      throw new RuntimeException("Failed to copy properties from " + from + " to " + to, t);
    }
  }
}
