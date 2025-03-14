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
package org.summerb.dbupgrade.impl;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageFactoryResolver;

/**
 * This impl will just find all beans of type {@link UpgradePackageFactory} except {@link
 * UpgradePackageFactoryDelegatingImpl}
 *
 * @author sergeyk
 */
public class UpgradePackageFactoryResolverSpringAutodiscoverImpl
    implements UpgradePackageFactoryResolver {
  @Autowired protected ApplicationContext ctx;
  protected List<UpgradePackageFactory> factories;

  @Override
  public List<UpgradePackageFactory> getFactories() {
    if (factories == null) {
      String[] names = ctx.getBeanNamesForType(UpgradePackageFactory.class);
      Preconditions.checkState(
          names.length > 0, "No beans of type UpgradePackageFactory have been found");
      List<UpgradePackageFactory> found =
          Arrays.stream(names)
              .filter(x -> !ctx.isTypeMatch(x, UpgradePackageFactoryDelegatingImpl.class))
              .map(x -> ctx.getBean(x, UpgradePackageFactory.class))
              .collect(Collectors.toList());
      Preconditions.checkState(
          !found.isEmpty(),
          "No beans of type UpgradePackageFactory have been found (UpgradePackageFactoryDelegatingImpl bean doesn't count)");
      this.factories = found;
    }
    return factories;
  }
}
