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
package org.summerb.easycrud.impl.wireTaps;

import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.EasyCrudWireTapMode;

/**
 * Use this is a base class for {@link EasyCrudWireTap}
 *
 * @author Sergey Karpushin
 * @param <TId> type of row id
 * @param <T> type of row
 */
public class EasyCrudWireTapAbstract<T> implements EasyCrudWireTap<T> {

  @Override
  public boolean requiresOnCreate() {
    return false;
  }

  @Override
  public void beforeCreate(T row) {}

  @Override
  public void afterCreate(T row) {}

  @Override
  public boolean requiresOnRead() {
    return false;
  }

  @Override
  public void beforeRead() {}

  @Override
  public void afterRead(T row) {}

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return EasyCrudWireTapMode.NOT_APPLICABLE;
  }

  @Override
  public void beforeUpdate(T from, T to) {}

  @Override
  public void afterUpdate(T from, T to) {}

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.NOT_APPLICABLE;
  }

  @Override
  public void beforeDelete(T row) {}

  @Override
  public void afterDelete(T row) {}
}
