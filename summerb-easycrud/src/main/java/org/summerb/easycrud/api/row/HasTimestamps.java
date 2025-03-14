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
package org.summerb.easycrud.api.row;

import org.summerb.easycrud.api.EasyCrudService;

/**
 * Let your DTO impl this interface. {@link EasyCrudService} then will set createdAt field upon
 * creation and update updatedAt on update.
 *
 * <p>Also it will make it possible to easily use optimistic locking technique when updating and
 * deleting rows via {@link EasyCrudService}
 *
 * @author sergey.karpushin
 */
public interface HasTimestamps {
  String FN_CREATED_AT = "createdAt";
  String FN_MODIFIED_AT = "modifiedAt";

  void setCreatedAt(long createdAt);

  long getCreatedAt();

  void setModifiedAt(long modifiedAt);

  long getModifiedAt();
}
