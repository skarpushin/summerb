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
package org.summerb.easycrud.api;

import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing operations on per-row basis. Which means that users will have different
 * access based on row data (or relevant data). Thus each row that user is attempting to access
 * needs to be checked.
 *
 * <p>Normally injected into {@link EasyCrudService} via {@link EasyCrudWireTapPerRowAuthImpl}, but
 * also can be used separately.
 *
 * <p>In case you do not need that detailed authorization rows you can use {@link
 * EasyCrudTableAuthStrategy}
 *
 * @author sergey.karpushin
 */
public interface EasyCrudPerRowAuthStrategy<TDto> {
  void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException;

  void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion)
      throws NotAuthorizedException;

  void assertAuthorizedToRead(TDto dto) throws NotAuthorizedException;

  void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException;
}
