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

/**
 * Impl this interface to indicate this DTO has auto-increment id.
 *
 * <p>{@link EasyCrudService} then will return a PK of a newly created row
 *
 * @author sergey.karpushin
 */
public interface HasAutoincrementId extends HasId<Long> {}
