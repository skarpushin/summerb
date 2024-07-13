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

/**
 * Common error codes for {@link EasyCrudService} operations
 *
 * @author sergey.karpushin
 */
public class EasyCrudMessageCodes {
  public static final String ENTITY_NOT_FOUND = "easycrud.entityNotFound";
  public static final String UNEXPECTED_FAILED_TO_CREATE = "easycrud.unexpecteFailed.create";
  public static final String UNEXPECTED_FAILED_TO_DELETE = "easycrud.unexpecteFailed.delete";
  public static final String UNEXPECTED_FAILED_TO_UPDATE = "easycrud.unexpecteFailed.update";
  public static final String UNEXPECTED_FAILED_TO_FIND = "easycrud.unexpecteFailed.find";
  public static final String EXCEPTION_DAO_CONCURRENT_MODIFICATION =
      "exception.dao.concurrentModification";
  public static final String EXCEPTION_DAO_DATA_TRUNCATION_ERROR =
      "exception.dao.dataTruncationError";
  public static final String VALIDATION_REFERENCED_ROW_CANNOT_BE_DELETED =
      "validation.referencedRowCannotBeDeleted";
  public static final String VALIDATION_REFERENCED_OBJECT_NOT_FOUND =
      "validation.referencedObjectNotFound";
}
