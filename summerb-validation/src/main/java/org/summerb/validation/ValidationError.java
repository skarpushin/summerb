/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.validation;

import java.io.Serializable;
import java.util.Arrays;

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;

/**
 * This class describes validation error. It could be subclassed to provide more details on error.
 *
 * <p>General idea is to not use here localized messages, only codes and typed subclasses if needed
 *
 * @author sergey.karpushin
 *     <p>TBD: Make this class abstract
 */
public class ValidationError implements Serializable, HasMessageCode, HasMessageArgs {
  private static final long serialVersionUID = 2414529436328740490L;

  /** Field token. Actually name of object field */
  private String fieldToken;

  /** Message about this field */
  private String messageCode;

  /** Message messageArgs */
  private Object[] messageArgs;

  /** @deprecated used only for serialization */
  @Deprecated
  public ValidationError() {}

  public ValidationError(String userMessageCode, String fieldToken) {
    if (userMessageCode == null || fieldToken == null) {
      throw new IllegalArgumentException("Message and field name token cannot be null.");
    }
    this.messageCode = userMessageCode;
    this.fieldToken = fieldToken;
  }

  public ValidationError(String userMessageCode, String fieldToken, Object... args) {
    this(userMessageCode, fieldToken);
    this.messageArgs = args;
  }

  @Override
  public String getMessageCode() {
    return messageCode;
  }

  public String getFieldToken() {
    return fieldToken;
  }

  public void setMessageCode(String userMessage) {
    this.messageCode = userMessage;
  }

  public void setFieldToken(String fieldToken) {
    this.fieldToken = fieldToken;
  }

  @Override
  public Object[] getMessageArgs() {
    return messageArgs;
  }

  @Override
  public String toString() {
    return ""
        + getClass().getSimpleName()
        + " (field = '"
        + getFieldToken()
        + "', msgCode = '"
        + getMessageCode()
        + "', msgArgs = '"
        + Arrays.toString(getMessageArgs())
        + "')";
  }
}
