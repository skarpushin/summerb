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
package org.summerb.easycrud.wireTaps;

import com.google.common.base.Preconditions;
import java.util.List;

/**
 * WireTap which will call all injected {@link EasyCrudWireTap} implementations
 *
 * @author sergeyk
 */
public class EasyCrudWireTapDelegatingImpl<TRow> implements EasyCrudWireTap<TRow> {
  protected List<EasyCrudWireTap<TRow>> chain;

  protected final boolean cacheEligibilityResponses;
  protected Boolean requiresOnCreate;
  protected Boolean requiresOnRead;
  protected Boolean requiresOnReadMultiple;
  protected EasyCrudWireTapMode requiresOnUpdate;
  protected EasyCrudWireTapMode requiresOnDelete;
  protected Boolean requiresOnDeleteMultiple;

  public EasyCrudWireTapDelegatingImpl(List<EasyCrudWireTap<TRow>> chain) {
    Preconditions.checkArgument(chain != null, "chain list must not be null");
    this.chain = chain;
    cacheEligibilityResponses = true;
  }

  public EasyCrudWireTapDelegatingImpl(
      List<EasyCrudWireTap<TRow>> chain, boolean cacheEligibilityResponses) {
    Preconditions.checkArgument(chain != null, "chain list must not be null");
    this.chain = chain;
    this.cacheEligibilityResponses = cacheEligibilityResponses;
  }

  @Override
  public boolean requiresOnCreate() {
    if (requiresOnCreate != null && cacheEligibilityResponses) {
      return requiresOnCreate;
    }

    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnCreate()) {
        return requiresOnCreate = true;
      }
    }
    return requiresOnCreate = false;
  }

  @Override
  public void beforeCreate(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.beforeCreate(row);
    }
  }

  @Override
  public void afterCreate(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterCreate(row);
    }
  }

  @Override
  public boolean requiresOnRead() {
    if (requiresOnRead != null && cacheEligibilityResponses) {
      return requiresOnRead;
    }

    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnRead()) {
        return requiresOnRead = true;
      }
    }
    return requiresOnRead = false;
  }

  @Override
  public void beforeRead() {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.beforeRead();
    }
  }

  @Override
  public void afterRead(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterRead(row);
    }
  }

  @Override
  public boolean requiresOnReadMultiple() {
    if (requiresOnReadMultiple != null && cacheEligibilityResponses) {
      return requiresOnReadMultiple;
    }

    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnReadMultiple()) {
        return requiresOnReadMultiple = true;
      }
    }
    return requiresOnReadMultiple = false;
  }

  @Override
  public void afterRead(List<TRow> rows) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterRead(rows);
    }
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    if (requiresOnUpdate != null && cacheEligibilityResponses) {
      return requiresOnUpdate;
    }

    EasyCrudWireTapMode ret = EasyCrudWireTapMode.NOT_APPLICABLE;
    for (EasyCrudWireTap<TRow> tap : chain) {
      ret = ret.max(tap.requiresOnUpdate());
    }
    return requiresOnUpdate = ret;
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.beforeUpdate(from, to);
    }
  }

  @Override
  public void afterUpdate(TRow from, TRow to) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterUpdate(from, to);
    }
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    if (requiresOnDelete != null && cacheEligibilityResponses) {
      return requiresOnDelete;
    }

    EasyCrudWireTapMode ret = EasyCrudWireTapMode.NOT_APPLICABLE;
    for (EasyCrudWireTap<TRow> tap : chain) {
      ret = ret.max(tap.requiresOnDelete());
    }
    return requiresOnDelete = ret;
  }

  @Override
  public void beforeDelete(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.beforeDelete(row);
    }
  }

  @Override
  public void afterDelete(TRow row) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterDelete(row);
    }
  }

  @Override
  public boolean requiresOnDeleteMultiple() {
    if (requiresOnDeleteMultiple != null && cacheEligibilityResponses) {
      return requiresOnDeleteMultiple;
    }

    for (EasyCrudWireTap<TRow> tap : chain) {
      if (tap.requiresOnDeleteMultiple()) {
        return requiresOnDeleteMultiple = true;
      }
    }
    return requiresOnDeleteMultiple = false;
  }

  @Override
  public void beforeDelete(List<TRow> rows) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.beforeDelete(rows);
    }
  }

  @Override
  public void afterDelete(List<TRow> rows) {
    for (EasyCrudWireTap<TRow> tap : chain) {
      tap.afterDelete(rows);
    }
  }

  public List<EasyCrudWireTap<TRow>> getChain() {
    return chain;
  }

  public void setChain(List<EasyCrudWireTap<TRow>> chain) {
    this.chain = chain;
  }
}
