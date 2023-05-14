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
package org.summerb.easycrud.api.dto;

import org.summerb.easycrud.api.EasyCrudService;

/**
 * Let your DTO impl this interface. {@link EasyCrudService} then will set createdAt field upon
 * creation and update updatedAt on update.
 *
 * <p>Also it will make it possible to easily use optimistic locking technique. See {@link
 * EasyCrudService#deleteByIdOptimistic(Object, long)}. Also {@link EasyCrudService#update(Object)}
 * will verify value of this field before updating row. Only if value of modifiedAt matches row will
 * be modified, otherwise operation will be considered a failure.
 *
 * @author sergey.karpushin
 */
public interface HasTimestamps {
  public static final String FN_CREATED_AT = "createdAt";
  public static final String FN_MODIFIED_AT = "modifiedAt";

  void setCreatedAt(long createdAt);

  long getCreatedAt();

  void setModifiedAt(long modifiedAt);

  long getModifiedAt();
}
