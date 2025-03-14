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

import java.sql.DataTruncation;
import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.i18n.HasMessageCode;

/** An Exception that is thrown when data submitted to DB is larger than field accepted size. */
public class ServiceDataTruncationException extends DataTruncation implements HasMessageCode {
  private static final long serialVersionUID = -8091580877575496130L;
  protected String fieldTokenBeingTruncated;
  protected String message;
  protected Throwable cause;

  /**
   * @deprecated used only for IO purposes
   */
  @Deprecated
  public ServiceDataTruncationException() {
    super(-1, false, false, 0, 0);
  }

  public ServiceDataTruncationException(String fieldTokenBeingTruncated, Throwable cause) {
    super(-1, false, false, 0, 0);
    this.fieldTokenBeingTruncated = fieldTokenBeingTruncated;

    message = "Data was too long for field '" + fieldTokenBeingTruncated + "'";
    this.cause = cause;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Throwable getCause() {
    return cause;
  }

  public static RuntimeException envelopeFor(String fieldName, Throwable cause) {
    DataTruncation dataTruncation = new ServiceDataTruncationException(fieldName, cause);
    throw new RuntimeException("Data truncation error was detected", dataTruncation);
  }

  @Override
  public String getMessageCode() {
    return EasyCrudMessageCodes.EXCEPTION_DAO_DATA_TRUNCATION_ERROR;
  }

  public String getFieldTokenBeingTruncated() {
    return fieldTokenBeingTruncated;
  }
}
