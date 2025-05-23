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
package org.summerb.spring.security;

import org.summerb.security.api.Roles;

public class SecurityConstants extends Roles {
  /** Default Permissions Domain */
  public static final String DOMAIN = "DD";

  public static final String MARKER_AWAITING_ACTIVATION = "ROLE_AWAITING_ACTIVATION";

  public static final String ROLE_CUSTOMER_CARE = "ROLE_CUSTOMER_CARE";
  public static final String ROLE_BACKGROUND_PROCESS = "ROLE_BACKGROUND_PROCESS";
}
