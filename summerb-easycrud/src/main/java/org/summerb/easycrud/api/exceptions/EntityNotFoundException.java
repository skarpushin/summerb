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
package org.summerb.easycrud.api.exceptions;

import java.io.Serial;
import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;
import org.summerb.i18n.exceptions.HasErrorDescriptionObject;

/**
 * Base class for exceptions for case when something wasn't found by its identity
 *
 * @author sergey.karpushin
 */
public class EntityNotFoundException extends RuntimeException
    implements HasMessageCode,
        HasMessageArgs,
        HasMessageArgsConverters,
        HasErrorDescriptionObject<EntityNotFoundResult> {
  @Serial private static final long serialVersionUID = 3254284449960233351L;

  private EntityNotFoundResult errorDescriptionObject;

  /**
   * @deprecated Used only for io
   */
  @Deprecated
  public EntityNotFoundException() {}

  public EntityNotFoundException(String entityMessageCode, Object identity) {
    super("Entity not found, code = " + entityMessageCode + ", id = " + identity);
    errorDescriptionObject = new EntityNotFoundResult(entityMessageCode, String.valueOf(identity));
  }

  public EntityNotFoundException(String entityMessageCode, Object identity, Throwable cause) {
    super("Entity not found, code = " + entityMessageCode + ", id = " + identity, cause);
    errorDescriptionObject = new EntityNotFoundResult(entityMessageCode, String.valueOf(identity));
  }

  @Override
  public Object[] getMessageArgs() {
    return new Object[] {
      errorDescriptionObject.getSubjectTypeMessageCode(), errorDescriptionObject.getIdentity()
    };
  }

  @Override
  public String getMessageCode() {
    return EasyCrudMessageCodes.ENTITY_NOT_FOUND;
  }

  @Override
  public MessageArgConverter[] getMessageArgsConverters() {
    return new MessageArgConverter[] {MessageCodeMessageArgConverter.INSTANCE, null};
  }

  @Override
  public EntityNotFoundResult getErrorDescriptionObject() {
    return errorDescriptionObject;
  }
}
