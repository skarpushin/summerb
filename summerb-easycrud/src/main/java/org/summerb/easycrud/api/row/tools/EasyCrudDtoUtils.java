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
package org.summerb.easycrud.api.row.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.summerb.easycrud.api.row.HasId;

public abstract class EasyCrudDtoUtils {
  private EasyCrudDtoUtils() {}

  public static <T, TDto extends HasId<T>> Set<T> enumerateIds(Iterable<TDto> iterable) {
    // TBD: Use streams
    Set<T> ret = new HashSet<>();
    for (HasId<T> row : iterable) {
      ret.add(row.getId());
    }
    return ret;
  }

  public static <TId, TDto extends HasId<TId>> Map<TId, TDto> toMapById(Iterable<TDto> iterable) {
    // TBD: Use streams
    Map<TId, TDto> ret = new HashMap<>();
    for (TDto row : iterable) {
      ret.put(row.getId(), row);
    }
    return ret;
  }
}
