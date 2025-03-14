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

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;

/**
 * @author sergey.karpushin
 */
public class EasyCrudUnexpectedException extends RuntimeException
    implements HasMessageCode, HasMessageArgs, HasMessageArgsConverters {
  private static final long serialVersionUID = 5053151069728043611L;
  protected String messageCode;
  protected String entityMessageCode;

  /**
   * @deprecated Used only for io
   */
  @Deprecated
  public EasyCrudUnexpectedException() {}

  public EasyCrudUnexpectedException(
      String messageCode, String entityMessageCode, Throwable cause) {
    super("Unexpected exception, code = " + messageCode + ", entity = " + entityMessageCode, cause);
    this.messageCode = messageCode;
    this.entityMessageCode = entityMessageCode;
  }

  @Override
  public MessageArgConverter[] getMessageArgsConverters() {
    return new MessageArgConverter[] {MessageCodeMessageArgConverter.INSTANCE};
  }

  @Override
  public Object[] getMessageArgs() {
    return new Object[] {entityMessageCode};
  }

  @Override
  public String getMessageCode() {
    return messageCode;
  }
}
