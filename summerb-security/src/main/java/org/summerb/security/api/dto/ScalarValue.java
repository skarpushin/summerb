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
package org.summerb.security.api.dto;

import java.io.Serializable;

public class ScalarValue<T> implements Serializable {
  private static final long serialVersionUID = 552482292114371317L;

  private T value;

  public static <T1> ScalarValue<T1> forV(T1 value) {
    ScalarValue<T1> ret = new ScalarValue<T1>();
    ret.value = value;
    return ret;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
