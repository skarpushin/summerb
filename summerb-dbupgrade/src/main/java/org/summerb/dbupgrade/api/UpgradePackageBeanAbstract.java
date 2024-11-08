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
package org.summerb.dbupgrade.api;

import org.springframework.context.annotation.Lazy;

/**
 * Special type of Upgrade package which represents a java bean. SO instead of making changes
 * described as sql, some "manual" java code changes will be applied.
 *
 * <p>It is recommended to mark such beans as {@link Lazy} so that they are instantiated only when
 * needed
 */
public abstract class UpgradePackageBeanAbstract implements UpgradePackageBean {
  private int id;
  private String name;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setMeta(int id, String name) {
    this.id = id;
    this.name = name;
  }
}
