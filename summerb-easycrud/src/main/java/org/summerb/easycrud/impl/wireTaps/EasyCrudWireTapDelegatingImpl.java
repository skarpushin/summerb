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

import java.util.List;

import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.EasyCrudWireTapMode;

import com.google.common.base.Preconditions;

/**
 * WireTap which will call all injected {@link EasyCrudWireTap} implementations
 *
 * @author sergeyk
 */
public class EasyCrudWireTapDelegatingImpl<TRow> implements EasyCrudWireTap<TRow> {
  protected List<EasyCrudWireTap<TRow>> chain;

  public EasyCrudWireTapDelegatingImpl(List<EasyCrudWireTap<TRow>> chain) {
    Preconditions.checkArgument(chain != null, "chain list must not be null");
    this.chain = chain;
  }

  @Override
  public boolean requiresOnCreate() {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnCreate()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void beforeCreate(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnCreate()) {
        continue;
      }
      tap.beforeCreate(row);
    }
  }

  @Override
  public void afterCreate(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnCreate()) {
        continue;
      }
      tap.afterCreate(row);
    }
  }

  @Override
  public boolean requiresOnRead() {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnRead()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void beforeRead() {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnRead()) {
        continue;
      }
      tap.beforeRead();
    }
  }

  @Override
  public void afterRead(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnRead()) {
        continue;
      }
      tap.afterRead(row);
    }
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    EasyCrudWireTapMode ret = EasyCrudWireTapMode.NOT_APPLICABLE;
    for (EasyCrudWireTap<TRow> tap : chain) {
      ret = ret.max(tap.requiresOnUpdate());
    }
    return ret;
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnUpdate().isNeeded()) {
        continue;
      }
      tap.beforeUpdate(from, to);
    }
  }

  @Override
  public void afterUpdate(TRow from, TRow to) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnUpdate().isNeeded()) {
        continue;
      }
      tap.afterUpdate(from, to);
    }
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    EasyCrudWireTapMode ret = EasyCrudWireTapMode.NOT_APPLICABLE;
    for (EasyCrudWireTap<TRow> tap : chain) {
      ret = ret.max(tap.requiresOnDelete());
    }
    return ret;
  }

  @Override
  public void beforeDelete(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      if (!tap.requiresOnUpdate().isNeeded()) {
        continue;
      }
      tap.beforeDelete(row);
    }
  }

  @Override
  public void afterDelete(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterDelete(row);
    }
  }

  public List<EasyCrudWireTap<TRow>> getChain() {
    return chain;
  }

  public void setChain(List<EasyCrudWireTap<TRow>> chain) {
    this.chain = chain;
  }
}
