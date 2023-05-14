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
package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.security.api.exceptions.NotAuthorizedException;

/**
 * @deprecated Use for testing purposes only
 * @author sergeyk
 */
@Deprecated
public class EasyCrudPerRowAuthStrategyNoOpImpl<TDto> implements EasyCrudPerRowAuthStrategy<TDto> {

  @Override
  public void assertAuthorizedToCreate(TDto dto) throws NotAuthorizedException {}

  @Override
  public void assertAuthorizedToUpdate(TDto existingVersion, TDto newVersion)
      throws NotAuthorizedException {}

  @Override
  public void assertAuthorizedToRead(TDto dto) throws NotAuthorizedException {}

  @Override
  public void assertAuthorizedToDelete(TDto dto) throws NotAuthorizedException {}
}
