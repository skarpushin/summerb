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
package org.summerb.easycrud.relations;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapMode;

/**
 * This impl will delegate calls to service responsible for referencers. It might get resource
 * consuming if referencer authorization wireTap requires full dto for authorization checks.
 *
 * <p>Whether rely on caching or provide your custom impl to optimize performance.
 *
 * @author sergeyk
 * @param <TId1> TId1
 * @param <TId2> TId2
 */
public class M2mAuthorizationWireTapImpl<TId1, TId2>
    extends EasyCrudWireTapAbstract<ManyToManyRow<TId1, TId2>> implements InitializingBean {
  protected EasyCrudWireTap<HasId<TId1>> referencerAuthorizationWireTap;
  protected EasyCrudService<TId1, HasId<TId1>> referencerService;
  protected Class<HasId<TId1>> referencerClass;

  public M2mAuthorizationWireTapImpl(
      EasyCrudService<TId1, HasId<TId1>> referencerService,
      EasyCrudWireTap<HasId<TId1>> referencerAuthorizationWireTap) {
    this.referencerService = referencerService;
    this.referencerAuthorizationWireTap = referencerAuthorizationWireTap;
  }

  @Override
  public void afterPropertiesSet() {
    Preconditions.checkArgument(referencerService != null, "referencerService required");
    Preconditions.checkArgument(
        referencerAuthorizationWireTap != null, "referencerAuthorizationWireTap required");

    referencerClass = referencerService.getRowClass();
  }

  public EasyCrudWireTap<HasId<TId1>> getReferencerAuthorizationWireTap() {
    return referencerAuthorizationWireTap;
  }

  public void setReferencerAuthorizationWireTap(
      EasyCrudWireTap<HasId<TId1>> referencerAuthorizationWireTap) {
    this.referencerAuthorizationWireTap = referencerAuthorizationWireTap;
  }

  public EasyCrudService<TId1, HasId<TId1>> getReferencerService() {
    return referencerService;
  }

  public void setReferencerService(EasyCrudService<TId1, HasId<TId1>> referencerService) {
    this.referencerService = referencerService;
  }

  @Override
  public boolean requiresOnCreate() {
    return referencerAuthorizationWireTap.requiresOnUpdate().isNeeded();
  }

  @Override
  public boolean requiresOnRead() {
    return referencerAuthorizationWireTap.requiresOnRead();
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return referencerAuthorizationWireTap.requiresOnUpdate();
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return referencerAuthorizationWireTap.requiresOnUpdate();
  }

  @Override
  public void beforeCreate(ManyToManyRow<TId1, TId2> row) {
    HasId<TId1> referencer;
    if (referencerAuthorizationWireTap.requiresOnUpdate().isDtoNeeded()) {
      referencer = referencerService.getById(row.getSrc());
    } else {
      referencer = buildDtoWithId(row.getSrc());
    }
    referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
  }

  @SuppressWarnings("deprecation")
  protected HasId<TId1> buildDtoWithId(TId1 id) {
    try {
      HasId<TId1> ret = referencerClass.newInstance();
      ret.setId(id);
      return ret;
    } catch (Throwable t) {
      throw new RuntimeException("Failed to build example dto", t);
    }
  }

  @Override
  public void beforeUpdate(ManyToManyRow<TId1, TId2> from, ManyToManyRow<TId1, TId2> to) {
    Preconditions.checkArgument(
        ObjectUtils.nullSafeEquals(from.getSrc(), to.getSrc()),
        "Referencer is not supposed to be changed");
    HasId<TId1> referencer;
    if (referencerAuthorizationWireTap.requiresOnUpdate().isDtoNeeded()) {
      referencer = referencerService.getById(to.getSrc());
    } else {
      referencer = buildDtoWithId(to.getSrc());
    }
    referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
  }

  @Override
  public void beforeDelete(ManyToManyRow<TId1, TId2> row) {
    HasId<TId1> referencer;
    if (referencerAuthorizationWireTap.requiresOnUpdate().isDtoNeeded()) {
      referencer = referencerService.getById(row.getSrc());
    } else {
      referencer = buildDtoWithId(row.getSrc());
    }
    referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
  }

  @Override
  public void afterRead(ManyToManyRow<TId1, TId2> row) {
    HasId<TId1> referencer;
    if (referencerAuthorizationWireTap.requiresOnUpdate().isDtoNeeded()) {
      referencer = referencerService.getById(row.getSrc());
    } else {
      referencer = buildDtoWithId(row.getSrc());
    }
    referencerAuthorizationWireTap.afterRead(referencer);
  }
}
