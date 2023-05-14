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
package org.summerb.dbupgrade.impl;

import org.springframework.context.ApplicationContext;
import org.summerb.dbupgrade.api.UpgradePackage;
import org.summerb.dbupgrade.api.UpgradePackageBean;
import org.summerb.dbupgrade.api.UpgradePackageFactory;
import org.summerb.dbupgrade.api.UpgradePackageMeta;

import com.google.common.base.Preconditions;

public class UpgradePackageFactoryBeanImpl implements UpgradePackageFactory {
  protected static final String EXTENSION = "bean";

  protected ApplicationContext applicationContext;

  public UpgradePackageFactoryBeanImpl(ApplicationContext applicationContext) {
    Preconditions.checkArgument(applicationContext != null, "applicationContext required");
    this.applicationContext = applicationContext;
  }

  @Override
  public UpgradePackage create(UpgradePackageMeta upgradePackageMeta) {
    Preconditions.checkArgument(
        supports(upgradePackageMeta), "Not supported: %s", upgradePackageMeta);
    UpgradePackageBean ret =
        applicationContext.getBean(upgradePackageMeta.getName(), UpgradePackageBean.class);
    ret.setMeta(upgradePackageMeta.getVersion(), upgradePackageMeta.getName());
    return ret;
  }

  @Override
  public boolean supports(UpgradePackageMeta upgradePackageMeta) {
    return EXTENSION.equalsIgnoreCase(upgradePackageMeta.getType())
        && applicationContext.isTypeMatch(upgradePackageMeta.getName(), UpgradePackageBean.class);
  }
}
