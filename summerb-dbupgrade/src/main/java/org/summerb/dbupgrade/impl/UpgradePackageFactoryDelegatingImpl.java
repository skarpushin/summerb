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
package org.summerb.dbupgrade.impl;

import java.util.function.Supplier;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageFactoryResolver;
import org.summerb.dbupgrade.api.UpgradePackageMeta;

public class UpgradePackageFactoryDelegatingImpl implements UpgradePackageFactory {
  protected UpgradePackageFactoryResolver upgradePackageFactory;

  public UpgradePackageFactoryDelegatingImpl(UpgradePackageFactoryResolver upgradePackageFactory) {
    this.upgradePackageFactory = upgradePackageFactory;
  }

  @Override
  public boolean supports(UpgradePackageMeta upgradePackageMeta) {
    return upgradePackageFactory.getFactories().stream()
        .anyMatch(x -> x.supports(upgradePackageMeta));
  }

  @Override
  public UpgradePackage create(UpgradePackageMeta upgradePackageMeta) {
    Supplier<? extends IllegalArgumentException> nfeException =
        () ->
            new IllegalArgumentException(
                "No UpgradePackageFactory found for " + upgradePackageMeta);
    return upgradePackageFactory.getFactories().stream()
        .filter(x -> x.supports(upgradePackageMeta))
        .map(x -> x.create(upgradePackageMeta))
        .findFirst()
        .orElseThrow(nfeException);
  }
}
