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
package org.summerb.easycrud.api.row;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.utils.DtoBase;

/**
 * Mark your DTO with this interface and clarify primary key type. {@link EasyCrudService} works
 * only with DTOs which impl this interface.
 *
 * @author sergey.karpushin
 * @param <TId> type of primary key
 */
public interface HasId<TId> extends DtoBase {
  public static final String FN_ID = "id";

  TId getId();

  void setId(TId id);
}
