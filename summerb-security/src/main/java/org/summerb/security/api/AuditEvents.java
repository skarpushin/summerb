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
package org.summerb.security.api;

import java.io.Serializable;

import org.summerb.security.api.dto.ScalarValue;

public interface AuditEvents {
  public static final String AUDIT_INJECTION_ATTEMPT = "INJ";

  /**
   * report record to audit log
   *
   * @param auditEventCode record type code. It's expected that all events with this code will have
   *     same type of data argument
   * @param data event data that will be serialized to audit log. Underlying implementation will
   *     choose serialization format. Default is JSON. If you want to put simple scalar value - it's
   *     recommended to use {@link ScalarValue} instance
   */
  void report(String auditEventCode, Serializable data);
}
