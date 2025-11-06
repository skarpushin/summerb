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
package org.summerb.easycrud.scaffold.impl;

import com.google.common.base.Preconditions;
import java.lang.reflect.Proxy;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.dao.EasyCrudDaoInjections;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.scaffold.EasyCrudServiceProxyFactory;
import org.summerb.easycrud.scaffold.ScaffoldedMethodFactory;

public class EasyCrudServiceProxyFactoryImpl implements EasyCrudServiceProxyFactory {
  protected ScaffoldedMethodFactory scaffoldedMethodFactory;

  public EasyCrudServiceProxyFactoryImpl(ScaffoldedMethodFactory scaffoldedMethodFactory) {
    this.scaffoldedMethodFactory = scaffoldedMethodFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>>
      TService createProxy(
          Class<TService> serviceInterface,
          EasyCrudService<TId, TDto> service,
          EasyCrudDaoInjections<TId, TDto> optionalDao) {

    Preconditions.checkArgument(serviceInterface != null, "interfaceType required");
    Preconditions.checkArgument(service != null, "service required");

    ClassLoader cl = serviceInterface.getClassLoader();
    Class<?>[] target = new Class<?>[] {serviceInterface};
    EasyCrudServiceScaffoldedImpl proxyImpl =
        new EasyCrudServiceScaffoldedImpl(
            scaffoldedMethodFactory,
            serviceInterface,
            (EasyCrudService<?, HasId<?>>) service,
            (EasyCrudDaoInjections<?, HasId<?>>) optionalDao);
    return (TService) Proxy.newProxyInstance(cl, target, proxyImpl);
  }
}
