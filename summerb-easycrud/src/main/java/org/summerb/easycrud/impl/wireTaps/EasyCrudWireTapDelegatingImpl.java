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
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

import com.google.common.base.Preconditions;

/**
 * WireTap which will call all injected {@link EasyCrudWireTap} implementations
 *
 * @author sergeyk
 */
public class EasyCrudWireTapDelegatingImpl<TId, TDto extends HasId<TId>>
    implements EasyCrudWireTap<TId, TDto> {
  private List<EasyCrudWireTap<TId, TDto>> chain;

  public EasyCrudWireTapDelegatingImpl(List<EasyCrudWireTap<TId, TDto>> chain) {
    Preconditions.checkArgument(chain != null, "chain list must not be null");
    this.chain = chain;
  }

  @Override
  public boolean requiresFullDto() {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      if (tap.requiresFullDto()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean requiresOnCreate() throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      if (tap.requiresOnCreate()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void beforeCreate(TDto dto) throws NotAuthorizedException, ValidationException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.beforeCreate(dto);
    }
  }

  @Override
  public void afterCreate(TDto dto) throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.afterCreate(dto);
    }
  }

  @Override
  public boolean requiresOnUpdate() throws NotAuthorizedException, ValidationException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      if (tap.requiresOnUpdate()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void beforeUpdate(TDto from, TDto to) throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.beforeUpdate(from, to);
    }
  }

  @Override
  public void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, ValidationException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.afterUpdate(from, to);
    }
  }

  @Override
  public boolean requiresOnDelete() throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      if (tap.requiresOnDelete()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void beforeDelete(TDto dto) throws NotAuthorizedException, ValidationException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.beforeDelete(dto);
    }
  }

  @Override
  public void afterDelete(TDto dto) throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.afterDelete(dto);
    }
  }

  @Override
  public boolean requiresOnRead() throws NotAuthorizedException, ValidationException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      if (tap.requiresOnRead()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void afterRead(TDto dto) throws ValidationException, NotAuthorizedException {
    for (EasyCrudWireTap<TId, TDto> tap : chain) {
      tap.afterRead(dto);
    }
  }

  public List<EasyCrudWireTap<TId, TDto>> getChain() {
    return chain;
  }

  public void setChain(List<EasyCrudWireTap<TId, TDto>> chain) {
    this.chain = chain;
  }
}
