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
package org.summerb.easycrud.impl.relations.example;

import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.easycrud.api.row.relations.RefQuantity;
import org.summerb.easycrud.impl.relations.ReferencesRegistryPredefinedImpl;

public class Refs extends ReferencesRegistryPredefinedImpl {
  public static final Ref deviceEnv =
      new Ref(
          "deviceEnv",
          DeviceService.TERM,
          DeviceRow.FN_ENV_ID,
          EnvService.TERM,
          HasId.FN_ID,
          RefQuantity.Many2One);

  public static final Ref envDevices =
      new Ref(
          "envDevices",
          EnvService.TERM,
          HasId.FN_ID,
          DeviceService.TERM,
          DeviceRow.FN_ENV_ID,
          RefQuantity.One2Many);

  public Refs() {
    super(deviceEnv, envDevices);
  }
}
