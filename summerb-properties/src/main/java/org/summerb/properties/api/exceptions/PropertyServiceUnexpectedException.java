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
package org.summerb.properties.api.exceptions;

import java.io.Serial;

public class PropertyServiceUnexpectedException extends RuntimeException {
  @Serial private static final long serialVersionUID = -755441433588285795L;

  public PropertyServiceUnexpectedException() {}

  public PropertyServiceUnexpectedException(String technicalMessage, Throwable cause) {
    super(technicalMessage, cause);
  }

  public PropertyServiceUnexpectedException(String technicalMessage) {
    super(technicalMessage);
  }
}
