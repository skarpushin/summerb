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
package org.summerb.easycrud.api.exceptions;

import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;

/**
 * This is a subclass of {@link EntityNotFoundException}. It adds the ability to conveniently record
 * type of missing object along with it's identity
 *
 * @author sergey.karpushin
 */
public class GenericEntityNotFoundException extends EntityNotFoundException
    implements HasMessageArgsConverters {
  private static final long serialVersionUID = -953061537781444391L;

  private String subjectTypeMessageCode;

  /**
   * @param subjectTypeMessageCode entity type message code
   * @param identity primary key value
   */
  public GenericEntityNotFoundException(String subjectTypeMessageCode, Object identity) {
    this(subjectTypeMessageCode, identity, null);
  }

  public GenericEntityNotFoundException(
      String subjectTypeMessageCode, Object identity, Throwable cause) {
    super(
        "Entity " + subjectTypeMessageCode + " identified by '" + identity + "' not found",
        identity,
        cause);
    this.setSubjectTypeMessageCode(subjectTypeMessageCode);
  }

  @Override
  public Object[] getMessageArgs() {
    return new Object[] {getSubjectTypeMessageCode(), getIdentity()};
  }

  @Override
  public String getMessageCode() {
    return EasyCrudMessageCodes.ENTITY_NOT_FOUND;
  }

  @Override
  public MessageArgConverter[] getMessageArgsConverters() {
    return new MessageArgConverter[] {MessageCodeMessageArgConverter.INSTANCE, null};
  }

  public String getSubjectTypeMessageCode() {
    return subjectTypeMessageCode;
  }

  public void setSubjectTypeMessageCode(String subjectTypeMessageCode) {
    this.subjectTypeMessageCode = subjectTypeMessageCode;
  }
}
