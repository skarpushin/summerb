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
import org.summerb.easycrud.impl.EasyCrudServiceImpl;

/**
 * This is just stub impl of {@link EasyCrudWireTap} to simplify {@link EasyCrudServiceImpl} logic
 *
 * @author Sergey Karpushin
 * @param <TRow> type of row
 */
public final class EasyCrudWireTapNoOpImpl<TRow> implements EasyCrudWireTap<TRow> {

  @Override
  public boolean requiresOnCreate() {
    return false;
  }

  @Override
  public void beforeCreate(TRow row) {}

  @Override
  public void afterCreate(TRow row) {}

  @Override
  public boolean requiresOnRead() {
    return false;
  }

  @Override
  public void afterRead(TRow row) {}

  @Override
  public void beforeRead() {}

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return EasyCrudWireTapMode.NOT_APPLICABLE;
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {}

  @Override
  public void afterUpdate(TRow from, TRow to) {}

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.NOT_APPLICABLE;
  }

  @Override
  public void beforeDelete(TRow row) {}

  @Override
  public void afterDelete(TRow row) {}
}
