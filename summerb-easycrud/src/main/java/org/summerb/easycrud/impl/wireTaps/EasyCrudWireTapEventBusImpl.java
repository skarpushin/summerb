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
package org.summerb.easycrud.impl.wireTaps;

import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.utils.DtoBase;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.tx.AfterCommitExecutorThreadLocalImpl;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * WireTap which will send {@link EntityChangedEvent} into injected {@link EventBus} after each
 * modification operation.
 *
 * <p>When using it in a transactional environment it's suggested to use {@link EventBus} with
 * {@link AfterCommitExecutorThreadLocalImpl} injected so that events will be send only after
 * transaction is committed.
 *
 * @author sergeyk
 */
public class EasyCrudWireTapEventBusImpl<TRow extends DtoBase>
    extends EasyCrudWireTapAbstract<TRow> {
  protected EventBus eventBus;

  public EasyCrudWireTapEventBusImpl(EventBus eventBus) {
    Preconditions.checkArgument(eventBus != null);
    this.eventBus = eventBus;
  }

  @Override
  public boolean requiresOnCreate() {
    return true;
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public void afterCreate(TRow row) {
    eventBus.post(EntityChangedEvent.added(row));
  }

  @Override
  public void afterUpdate(TRow from, TRow to) {
    eventBus.post(EntityChangedEvent.updated(to));
  }

  @Override
  public void afterDelete(TRow row) {
    eventBus.post(EntityChangedEvent.removedObject(row));
  }
}
