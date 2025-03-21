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
package org.summerb.webappboilerplate.security.impls;

import com.google.common.base.Preconditions;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.security.api.CurrentUserNotFoundException;
import org.summerb.spring.security.impl.SecurityContextResolverAbstract;
import org.summerb.users.api.dto.User;

// TZD: Add template parameter for user type
public class SecurityContextResolverImpl extends SecurityContextResolverAbstract<User> {

  @Override
  public String getUserUuid() throws CurrentUserNotFoundException {
    return getUser().getUuid();
  }

  @Override
  protected User getUserFromUserDetails(UserDetails principal) {
    Preconditions.checkArgument(principal instanceof UserDetailsImpl);
    return ((UserDetailsImpl) principal).getUser();
  }
}
