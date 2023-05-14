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
package org.summerb.users.impl.dao;

import org.springframework.dao.DuplicateKeyException;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.users.api.dto.User;

public interface UserDao {
	void createUser(User user) throws DuplicateKeyException;

	User findUserByUuid(String userUuid);

	User findUserByEmail(String userEmail);

	PaginatedList<User> findUserByDisplayNamePartial(String displayNamePartial, PagerParams pagerParams);

	boolean updateUser(User user) throws DuplicateKeyException;

	boolean deleteUser(String userUuid);
}
