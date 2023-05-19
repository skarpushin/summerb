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

import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.easycrud.api.EasyCrudWireTapMode;

import com.google.common.base.Preconditions;

/**
 * WireTap which will invoke injected {@link EasyCrudValidationStrategy} before row create and
 * update operations.
 *
 * @author sergeyk
 */
public class EasyCrudWireTapValidationImpl<TRow> extends EasyCrudWireTapAbstract<TRow> {
  protected EasyCrudValidationStrategy<TRow> strategy;

  public EasyCrudWireTapValidationImpl(EasyCrudValidationStrategy<TRow> strategy) {
    Preconditions.checkArgument(strategy != null);
    this.strategy = strategy;
  }

  @Override
  public boolean requiresOnCreate() {
    return true;
  }

  @Override
  public void beforeCreate(TRow row) {
    strategy.validateForCreate(row);
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return strategy.isCurrentlyPersistedRowNeededForUpdateValidation()
        ? EasyCrudWireTapMode.FULL_DTO_AND_CURRENT_VERSION_NEEDED
        : EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {
    strategy.validateForUpdate(from, to);
  }
}
