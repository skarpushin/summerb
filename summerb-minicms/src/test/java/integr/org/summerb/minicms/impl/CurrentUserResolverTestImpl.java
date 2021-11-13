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
package integr.org.summerb.minicms.impl;

import java.util.Date;
import java.util.UUID;

import org.summerb.security.api.CurrentUserResolver;
import org.summerb.users.api.dto.User;

public class CurrentUserResolverTestImpl implements CurrentUserResolver {
	public User user;
	public User user1;
	public User user2;

	public CurrentUserResolverTestImpl() {
		user1 = new User();
		user1.setUuid(UUID.randomUUID().toString());
		user1.setEmail("test" + new Date().getTime() + "@test.org");
		user1.setDisplayName("Display name");

		user2 = new User();
		user2.setUuid(UUID.randomUUID().toString());
		user2.setEmail("test" + (new Date().getTime() + 20) + "@test.org");
		user2.setDisplayName("Display name 2");

		user = user1;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public String getUserUuid() {
		return user.getUuid();
	}

}
