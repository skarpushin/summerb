/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package integr.org.summerb.easycrud;

import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.security.api.exceptions.NotAuthorizedException;

public class TestDto2PerRowAuthImpl implements EasyCrudPerRowAuthStrategy<TestDto2> {
	@Override
	public void assertAuthorizedToCreate(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnCreate".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "create");
		}
	}

	@Override
	public void assertAuthorizedToUpdate(TestDto2 existingVersion, TestDto2 newVersion) throws NotAuthorizedException {
		if ("throwNaeOnUpdate".equals(existingVersion.getEnv())) {
			throw new NotAuthorizedException("test", "update", "" + existingVersion.getId());
		}
		if ("throwNaeForUpdate".equals(newVersion.getEnv())) {
			throw new NotAuthorizedException("test", "update", "" + existingVersion.getId());
		}
	}

	@Override
	public void assertAuthorizedToRead(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnRead".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "read", "" + dto.getId());
		}
	}

	@Override
	public void assertAuthorizedToDelete(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnDelete".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "delete", "" + dto.getId());
		}
	}
}
