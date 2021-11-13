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
package org.summerb.easycrud.api;

import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;

/**
 * Strategy for authorizing table-wide operation. Used in case when all rows
 * have same authorization rules.
 * 
 * <p>
 * 
 * Normally injected into {@link EasyCrudService} via {@link EasyCrudWireTap}
 * (particularly {@link EasyCrudWireTapPerRowAuthImpl} ), but also can be used
 * separately.
 * 
 * <p>
 * 
 * In case you need per-row authorization rules use
 * {@link EasyCrudPerRowAuthStrategy}.
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudTableAuthStrategy {
	void assertAuthorizedToCreate() throws NotAuthorizedException;

	void assertAuthorizedToUpdate() throws NotAuthorizedException;

	void assertAuthorizedToRead() throws NotAuthorizedException;

	void assertAuthorizedToDelete() throws NotAuthorizedException;
}
