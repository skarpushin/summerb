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
package org.summerb.users.api.dto;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.summerb.users.impl.dom.Password;

public class PasswordFactory {
  public static PasswordEncoder passwordEncoder = new StandardPasswordEncoder("test");
  public static final String RIGHT_PASSWORD_FOR_EXISTENT_USER = "passwordRight";
  public static String RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH;
  public static final String TOKEN_FOR_EXISTENT_USER = UUID.randomUUID().toString();
  public static final String NOT_EXISTENT_RESTORATION_TOKEN = UUID.randomUUID().toString();

  static {
    RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH =
        passwordEncoder.encode(RIGHT_PASSWORD_FOR_EXISTENT_USER);
  }

  private PasswordFactory() {}

  public static Password createExistentUserPassword() {
    Password ret = new Password();
    ret.setUserUuid(UserFactory.EXISTENT_USER);
    ret.setPasswordHash(RIGHT_PASSWORD_FOR_EXISTENT_USER_HASH);
    ret.setRestorationToken(TOKEN_FOR_EXISTENT_USER);
    return ret;
  }
}
