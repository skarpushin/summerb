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

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.tx.AfterCommitExecutorThreadLocalImpl;
import org.summerb.validation.ValidationException;

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
public class EasyCrudWireTapEventBusImpl<TId, TDto extends HasId<TId>>
    extends EasyCrudWireTapNoOpImpl<TId, TDto> {
  private EventBus eventBus;

  @Autowired
  public EasyCrudWireTapEventBusImpl(EventBus eventBus) {
    Preconditions.checkArgument(eventBus != null);
    this.eventBus = eventBus;
  }

  @Override
  public boolean requiresOnCreate() throws ValidationException, NotAuthorizedException {
    return true;
  }

  @Override
  public void afterCreate(TDto dto) throws ValidationException, NotAuthorizedException {
    eventBus.post(EntityChangedEvent.added(dto));
  }

  @Override
  public boolean requiresOnUpdate() throws NotAuthorizedException, ValidationException {
    return true;
  }

  @Override
  public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, ValidationException {
    eventBus.post(EntityChangedEvent.updated(to));
  }

  @Override
  public boolean requiresOnDelete() throws ValidationException, NotAuthorizedException {
    return true;
  }

  @Override
  public void afterDelete(TDto dto) throws ValidationException, NotAuthorizedException {
    eventBus.post(EntityChangedEvent.removedObject(dto));
  }

  @Override
  public boolean requiresOnRead() throws NotAuthorizedException, ValidationException {
    return false;
  }
}
