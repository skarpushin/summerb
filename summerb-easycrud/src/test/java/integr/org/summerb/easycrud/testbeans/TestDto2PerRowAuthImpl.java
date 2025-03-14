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
package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.TestDto2;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.security.api.dto.NotAuthorizedResult;

public class TestDto2PerRowAuthImpl extends EasyCrudAuthorizationPerRowStrategy<TestDto2> {
  @Override
  public NotAuthorizedResult getForCreate(TestDto2 row) {
    if ("throwNaeOnCreate".equals(row.getEnv())) {
      return denyCreate(row);
    }
    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForRead(TestDto2 row) {
    if ("throwNaeOnRead".equals(row.getEnv())) {
      return denyRead(row);
    }
    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForUpdate(TestDto2 persistedVersion, TestDto2 row) {
    if ("throwNaeOnUpdate".equals(persistedVersion.getEnv())) {
      return denyUpdate(persistedVersion);
    }
    if ("throwNaeForUpdate".equals(row.getEnv())) {
      return denyUpdate(row);
    }

    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForDelete(TestDto2 row) {
    if ("throwNaeOnDelete".equals(row.getEnv())) {
      return denyDelete(row);
    }
    return ALLOW;
  }
}
