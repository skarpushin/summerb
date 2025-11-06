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
package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.PostRow;
import org.summerb.easycrud.auth.legacy.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.security.api.dto.NotAuthorizedResult;

public class PostPerRowAuthImpl extends EasyCrudAuthorizationPerRowStrategy<PostRow> {
  @Override
  public NotAuthorizedResult getForCreate(PostRow row) {
    if ("throwNaeOnCreate".equals(row.getTitle())) {
      return denyCreate(row);
    }
    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForRead(PostRow row) {
    if ("throwNaeOnRead".equals(row.getTitle())) {
      return denyRead(row);
    }
    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForUpdate(PostRow persistedVersion, PostRow row) {
    if ("throwNaeOnUpdate".equals(persistedVersion.getTitle())) {
      return denyUpdate(persistedVersion);
    }
    if ("throwNaeForUpdate".equals(row.getTitle())) {
      return denyUpdate(row);
    }

    return ALLOW;
  }

  @Override
  public NotAuthorizedResult getForDelete(PostRow row) {
    if ("throwNaeOnDelete".equals(row.getTitle())) {
      return denyDelete(row);
    }
    return ALLOW;
  }
}
